package com.example.readyauction.service.auction;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.readyauction.controller.request.auction.BidRequest;
import com.example.readyauction.controller.response.auction.BidResponse;
import com.example.readyauction.controller.response.product.ProductFindResponse;
import com.example.readyauction.domain.user.CustomUserDetails;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.exception.auction.BiddingFailException;
import com.example.readyauction.exception.auction.RedisLockAcquisitionException;
import com.example.readyauction.exception.auction.RedisLockOperationException;
import com.example.readyauction.repository.auction.AuctionRepository;
import com.example.readyauction.repository.auction.EmitterRepository;
import com.example.readyauction.service.product.ProductFacade;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final EmitterRepository emitterRepository;
    private final ProductFacade productFacade;
    private final RedissonClient redissonClient;

    public AuctionService(AuctionRepository auctionRepository, EmitterRepository emitterRepository,
        ProductFacade productFacade, RedissonClient redissonClient) {
        this.auctionRepository = auctionRepository;
        this.emitterRepository = emitterRepository;
        this.productFacade = productFacade;
        this.redissonClient = redissonClient;
    }

    @Transactional
    public SseEmitter subscribe(CustomUserDetails user, Long productId) {
        LocalDateTime closeDate = getCloseDate(productId);
        SseEmitter emitter = createEmitter(user.getUser(), productId, closeDate);

        // 503 에러 방지를 위한 데이터 전송
        // Emitter를 생성하고 나서 만료 시간까지 아무런 데이터도 보내지 않으면 재연결 요청시 503 Service Unavailable 에러가 발생할 수 있음.
        send(emitter, user.getUser(), productId, "SSE Emitter Created. [userId=" + user.getUser().getUserId() + "]",
            "SSE CONNECT.");
        return emitter;
    }

    @Transactional
    public void subscribeCancel(CustomUserDetails user, Long productId) {
        // 특정 USER의 Emitter를 제거
        emitterRepository.deleteEmitter(user.getUser(), productId);

    }

    @Transactional
    public BidResponse biddingPrice(CustomUserDetails user, BidRequest bidRequest, Long productId) {
        RLock lock = redissonClient.getLock("lock:" + productId);
        Long currentHighestPrice = null;

        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                throw new RedisLockAcquisitionException(productId);
            }
            currentHighestPrice = processBid(user, bidRequest, productId);
        } catch (Exception e) {
            throw new RedisLockOperationException(productId, e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return BidResponse.from(productId,
            calculateIncreaseRate(productId, currentHighestPrice, bidRequest.getBiddingPrice()));
    }

    private Long processBid(CustomUserDetails user, BidRequest bidRequest, Long productId) {
        Long currentHighestPrice = 0L;

        RMap<Long, Pair<Long, Long>> highestBidMap = redissonClient.getMap(
            String.valueOf(productId));// productId : (userId, bestPrice)
        Pair<Long, Long> userIdAndCurrentPrice = highestBidMap.get(productId); // (userId, 최고가) 가져오기

        if (highestBidMap == null) { // 최초 입찰
            updateRedisBidData(user, highestBidMap, bidRequest, productId);
            return Long.valueOf(bidRequest.getBiddingPrice());
        } else {
            currentHighestPrice = userIdAndCurrentPrice.getSecond();

            if (bidRequest.getBiddingPrice() <= currentHighestPrice) {
                throw new BiddingFailException(productId);
            }

            updateRedisBidData(user, highestBidMap, bidRequest, productId);
            sendToAllUsers(productId, "최고가가 " + bidRequest.getBiddingPrice() + "원으로 올랐습니다.", "최고가 수정 알림");
        }
        return currentHighestPrice;
    }

    private void updateRedisBidData(CustomUserDetails user, RMap<Long, Pair<Long, Long>> bidMap, BidRequest bidRequest,
        Long productId) {
        Pair<Long, Long> newPair = Pair.of(user.getUser().getId(), Long.valueOf(bidRequest.getBiddingPrice()));
        bidMap.put(productId, newPair); // productId에 대한 최고가 정보 업데이트
    }

    // 해당 경매에 참여한 User들에게 해당 경매 상품의 최고가가 갱신될때마다 SSE 알림.
    private void sendToAllUsers(Long productId, Object data, String comment) {
        Map<User, SseEmitter> sseEmitterMap = emitterRepository.get(productId);
        for (Map.Entry<User, SseEmitter> entry : sseEmitterMap.entrySet()) {
            User user = entry.getKey();
            SseEmitter sseEmitter = entry.getValue();
            send(sseEmitter, user, productId, data, comment);
        }
    }

    private void send(SseEmitter sseEmitter, User user, Long productId, Object data, String comment) {
        try {
            sseEmitter.send(SseEmitter.event() // SSE 이벤트를 생성하고 해당 Emitter로 전송합.
                .id(productId.toString()) // 이벤트의 고유 ID (문자열 형태로 변환)
                .name("THE HIGHEST PRICE UPDATE") // 이벤트의 이름을 "THE HIGHEST PRICE UPDATE"로 지정
                .data(data) // 전송할 데이터
                .comment(comment)); // 이벤트에 대한 코멘트
        } catch (IOException e) {
            emitterRepository.deleteEmitter(user, productId);
            sseEmitter.completeWithError(e);
        }
    }

    private SseEmitter createEmitter(User user, Long productId, LocalDateTime closeDate) {
        // 경매가 끝나기 전까지 계속 최고가 알람을 줘야해서 경매 종료일 - 요청 시간 = SSE TimeOut
        Long requestTime = convertTimeStamp(LocalDateTime.now());
        Long timeOut = convertTimeStamp(closeDate) - requestTime;

        SseEmitter emitter = new SseEmitter(timeOut);
        emitterRepository.save(productId, user, emitter);
        emitter.onCompletion(() -> emitterRepository.deleteEmitter(user, productId));
        emitter.onTimeout(() -> emitterRepository.deleteEmitter(user, productId));

        return emitter;
    }

    private LocalDateTime getCloseDate(Long productId) {
        return productFacade.findById(productId).getCloseDate();
    }

    private Long convertTimeStamp(LocalDateTime time) {
        // ZoneId.systemDefault()) vs ZoneId.of("Asia/Seoul"))
        return time.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
    }

    private double calculateIncreaseRate(Long productId, Long previousPrice, int nextPrice) {
        if (previousPrice == nextPrice) {
            ProductFindResponse product = productFacade.findById(productId);
            return ((double)(nextPrice - product.getStartPrice()) / previousPrice) * 100;
        }
        return ((double)(nextPrice - previousPrice) / previousPrice) * 100;
    }

}
