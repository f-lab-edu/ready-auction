package com.example.readyauction.service.auction;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.readyauction.controller.request.auction.TenderRequest;
import com.example.readyauction.controller.response.auction.TenderResponse;
import com.example.readyauction.controller.response.product.ProductFindResponse;
import com.example.readyauction.domain.auction.Auction;
import com.example.readyauction.domain.user.CustomUserDetails;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.exception.auction.BiddingFailException;
import com.example.readyauction.repository.auction.AuctionRepository;
import com.example.readyauction.repository.auction.EmitterRepository;
import com.example.readyauction.service.product.ProductFacade;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final EmitterRepository emitterRepository;
    private final ProductFacade productFacade;

    public AuctionService(AuctionRepository auctionRepository, EmitterRepository emitterRepository,
        ProductFacade productFacade) {
        this.auctionRepository = auctionRepository;
        this.emitterRepository = emitterRepository;
        this.productFacade = productFacade;
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
    public TenderResponse tenderPrice(CustomUserDetails user, TenderRequest tenderRequest, Long productId) {
        // 여기서 최고가로 갱신되면 sendToUser()
        Optional<Auction> findAuction = auctionRepository.findById(productId);
        if (findAuction.get() == null) { // null이면 아직 최고가를 제안한 사람이 없다는 것
            Auction newAuction = Auction.builder()
                .userId(user.getUser().getUserId())
                .productId(productId)
                .bestPrice(tenderRequest.getBiddingPrice())
                .build();
            auctionRepository.save(newAuction);
            return TenderResponse.builder()
                .productId(productId)
                .rateOfIncrease(calculateIncreaseRate(productId, -1, tenderRequest.getBiddingPrice()))
                .build();
        } else { // 있다는건 최고가를 제안한 사람이 있다는 것
            Auction action = findAuction.get();

            // 동시성 제어가 필요한 부분?
            if (tenderRequest.getBiddingPrice() < action.getBestPrice()) {
                throw new BiddingFailException(productId);
            } else {
                action.updateActionInfo(user.getUser().getUserId(), tenderRequest.getBiddingPrice());
                sendToAllUsers(productId, "최고가가 " + tenderRequest.getBiddingPrice() + "원으로 올랐습니다.", "최고가 수정 알림");
            }

            return TenderResponse.builder()
                .productId(productId)
                .rateOfIncrease(
                    calculateIncreaseRate(productId, action.getBestPrice(), tenderRequest.getBiddingPrice()))
                .build();
        }
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
                .name("BEST PRICE UPDATE") // 이벤트의 이름을 "sse"로 지정
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
        // [나중에 추가 예정] 사용자가 "최고가 알람 끄기" 선택하면 delete
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

    private double calculateIncreaseRate(Long productId, int previousPrice, int nextPrice) {
        if (previousPrice == -1) {
            ProductFindResponse product = productFacade.findById(productId);
            return ((double)(nextPrice - product.getStartPrice()) / previousPrice) * 100;
        }
        return ((double)(nextPrice - previousPrice) / previousPrice) * 100;
    }

}
