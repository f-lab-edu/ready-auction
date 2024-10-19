package com.example.readyauction.service.auction;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.readyauction.controller.request.auction.BidRequest;
import com.example.readyauction.controller.response.auction.BidResponse;
import com.example.readyauction.controller.response.product.ProductFindResponse;
import com.example.readyauction.domain.user.CustomUserDetails;
import com.example.readyauction.exception.auction.BiddingFailException;
import com.example.readyauction.exception.auction.RedisLockAcquisitionException;
import com.example.readyauction.exception.auction.RedisLockInterruptedException;
import com.example.readyauction.service.product.ProductFacade;

@Service
public class AuctionService {

    private final ProductFacade productFacade;
    private final HighestBidSseNotificationService bidSseNotificationService;
    private final RedissonClient redissonClient;

    public AuctionService(ProductFacade productFacade, HighestBidSseNotificationService bidSseNotificationService,
        RedissonClient redissonClient) {
        this.productFacade = productFacade;
        this.bidSseNotificationService = bidSseNotificationService;
        this.redissonClient = redissonClient;
    }

    @Transactional
    public BidResponse biddingPrice(CustomUserDetails user, BidRequest bidRequest, Long productId) {
        isBiddingAvailable(user, bidRequest, productId);

        RLock lock = redissonClient.getLock("lock:" + productId);
        Long currentHighestPrice;

        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                throw new RedisLockAcquisitionException(productId);
            }
            currentHighestPrice = processBid(user, bidRequest, productId);
            bidSseNotificationService.sendToAllUsers(productId, "최고가가 " + currentHighestPrice + "원으로 올랐습니다.",
                "최고가 수정 알림");
        } catch (InterruptedException e) {
            throw new RedisLockInterruptedException(productId, e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return BidResponse.from(productId,
            calculateIncreaseRate(productId, currentHighestPrice, bidRequest.getBiddingPrice()));
    }

    private Long processBid(CustomUserDetails user, BidRequest bidRequest, Long productId) {

        RMap<Long, Pair<Long, Long>> highestBidMap = redissonClient.getMap(
            String.valueOf(productId));// productId : (userId, bestPrice)
        Pair<Long, Long> userIdAndCurrentPrice = highestBidMap.get(productId); // (userId, 최고가) 가져오기

        if (userIdAndCurrentPrice == null) { // 최초 입찰
            return updateRedisBidData(user, highestBidMap, bidRequest, productId);
        }

        if (bidRequest.getBiddingPrice() <= userIdAndCurrentPrice.getSecond()) {
            throw new BiddingFailException(user.getUser().getUserId(), bidRequest.getBiddingPrice(), productId);
        }

        updateRedisBidData(user, highestBidMap, bidRequest, productId);
        return updateRedisBidData(user, highestBidMap, bidRequest, productId);
    }

    private void isBiddingAvailable(CustomUserDetails user, BidRequest bidRequest, Long productId) {
        LocalDateTime biddingRequestTime = LocalDateTime.now();
        ProductFindResponse product = productFacade.findById(productId, biddingRequestTime);
        if (biddingRequestTime.isAfter(product.getCloseDate())) {
            throw new BiddingFailException(user.getUser().getUserId(), bidRequest.getBiddingPrice(), productId);
        }
    }

    private Long updateRedisBidData(CustomUserDetails user, RMap<Long, Pair<Long, Long>> bidMap, BidRequest bidRequest,
        Long productId) {
        Pair<Long, Long> newPair = Pair.of(user.getUser().getId(), Long.valueOf(bidRequest.getBiddingPrice()));
        bidMap.put(productId, newPair); // productId에 대한 최고가 정보 업데이트
        return Long.valueOf(bidRequest.getBiddingPrice());
    }

    private double calculateIncreaseRate(Long productId, Long previousPrice, int nextPrice) {
        if (previousPrice == nextPrice) { // 최초 입찰
            ProductFindResponse product = productFacade.findById(productId, LocalDateTime.now());
            return ((double)(nextPrice - product.getStartPrice()) / previousPrice) * 100;
        }
        return ((double)(nextPrice - previousPrice) / previousPrice) * 100;
    }

}
