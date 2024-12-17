package com.example.moduleapi.service.auction;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.moduleapi.controller.request.auction.BidRequest;
import com.example.moduleapi.controller.request.point.PointAmount;
import com.example.moduleapi.controller.response.auction.BidResponse;
import com.example.moduleapi.controller.response.product.ProductFindResponse;
import com.example.moduleapi.exception.auction.BiddingFailException;
import com.example.moduleapi.exception.auction.RedisLockAcquisitionException;
import com.example.moduleapi.exception.auction.RedisLockInterruptedException;
import com.example.moduleapi.service.point.PointService;
import com.example.moduleapi.service.product.ProductFacade;
import com.example.moduledomain.domain.bidLogging.BidLogging;
import com.example.moduledomain.domain.user.CustomUserDetails;
import com.example.moduledomain.domain.user.Gender;
import com.example.moduledomain.domain.user.User;

@Service
public class AuctionService {

    private final ProductFacade productFacade;
    private final HighestBidSseNotificationService bidSseNotificationService;
    private final RedissonClient redissonClient;
    private final KafkaProducerService kafkaProducerService;
    private final BidLoggingService bidLoggingService;
    private final PointService pointService;

    public AuctionService(ProductFacade productFacade, HighestBidSseNotificationService bidSseNotificationService,
        RedissonClient redissonClient, KafkaProducerService kafkaProducerService, BidLoggingService bidLoggingService,
        PointService pointService) {
        this.productFacade = productFacade;
        this.bidSseNotificationService = bidSseNotificationService;
        this.redissonClient = redissonClient;
        this.kafkaProducerService = kafkaProducerService;
        this.bidLoggingService = bidLoggingService;
        this.pointService = pointService;
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
            kafkaProducerService.publishAuctionPriceChangeNotification(productId, currentHighestPrice);
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

    @Transactional
    public Optional<Pair<Long, Long>> getAuctionUserInfoByProductId(Long productId) {
        RMap<Long, Pair<Long, Long>> highestBidMap = redissonClient.getMap(
            String.valueOf(productId)); // productId : (userId, bestPrice)
        Pair<Long, Long> userIdAndCurrentPrice = highestBidMap.get(productId); // (userId, 최고가) 가져오기
        return Optional.ofNullable(userIdAndCurrentPrice); // (userId, 최고가) 가져오기
    }

    private Long processBid(CustomUserDetails customUserDetails, BidRequest bidRequest, Long productId) {

        RMap<Long, Pair<Long, Long>> highestBidMap = redissonClient.getMap(
            String.valueOf(productId));// productId : (userId, bestPrice)
        Pair<Long, Long> userIdAndCurrentPrice = highestBidMap.get(productId); // (userId, 최고가) 가져오기
        User user = customUserDetails.getUser();

        boolean isAuctionSuccessful = isAuctionSuccessful(userIdAndCurrentPrice, bidRequest);
        BidLogging bidLogging = createBidLogging(user.getId(), productId, user.getGender(),
            bidRequest.getBiddingPrice(), user.getAge(), isAuctionSuccessful);
        bidLoggingService.logging(bidLogging);

        if (userIdAndCurrentPrice == null) { // 최초 입찰
            return updateRedisBidData(customUserDetails, highestBidMap, bidRequest, productId);
        }
        if (bidRequest.getBiddingPrice() <= userIdAndCurrentPrice.getSecond()) {
            pointService.rollbackPoint(user.getId(), bidRequest.getBiddingPrice());
            throw new BiddingFailException(user.getUserId(), bidRequest.getBiddingPrice(), productId);
        }

        pointService.rollbackPoint(userIdAndCurrentPrice.getFirst(), userIdAndCurrentPrice.getSecond().intValue());
        return updateRedisBidData(customUserDetails, highestBidMap, bidRequest, productId);
    }

    private void isBiddingAvailable(CustomUserDetails user, BidRequest bidRequest, Long productId) {
        LocalDateTime biddingRequestTime = LocalDateTime.now();
        ProductFindResponse product = productFacade.findById(productId);
        if (biddingRequestTime.isAfter(product.getCloseDate())) {
            throw new BiddingFailException(user.getUser().getUserId(), bidRequest.getBiddingPrice(), productId);
        }

        PointAmount pointAmount = new PointAmount(bidRequest.getBiddingPrice());
        pointService.deductPoint(user, pointAmount);
    }

    private Long updateRedisBidData(CustomUserDetails user, RMap<Long, Pair<Long, Long>> bidMap, BidRequest bidRequest,
        Long productId) {
        Pair<Long, Long> newPair = Pair.of(user.getUser().getId(), Long.valueOf(bidRequest.getBiddingPrice()));
        bidMap.put(productId, newPair); // productId에 대한 최고가 정보 업데이트
        return Long.valueOf(bidRequest.getBiddingPrice());
    }

    private double calculateIncreaseRate(Long productId, Long previousPrice, int nextPrice) {
        if (previousPrice == nextPrice) { // 최초 입찰
            ProductFindResponse product = productFacade.findById(productId);
            return ((double)(nextPrice - product.getStartPrice()) / previousPrice) * 100;
        }
        return ((double)(nextPrice - previousPrice) / previousPrice) * 100;
    }

    private BidLogging createBidLogging(Long userId, Long productId, Gender gender, int age, int price,
        boolean isAuctionSuccessful) {
        ProductFindResponse product = productFacade.findById(productId);
        return BidLogging.builder()
            .userId(userId)
            .productId(productId)
            .category(product.getCategory())
            .age(age)
            .gender(gender)
            .price(price)
            .isAuctionSuccessful(isAuctionSuccessful)
            .build();
    }

    private boolean isAuctionSuccessful(Pair<Long, Long> userIdAndCurrentPrice, BidRequest bidRequest) {
        if (userIdAndCurrentPrice == null) {
            return true;
        }
        return bidRequest.getBiddingPrice() > userIdAndCurrentPrice.getSecond();
    }

}
