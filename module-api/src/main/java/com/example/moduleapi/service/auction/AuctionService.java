package com.example.moduleapi.service.auction;

import com.example.moduleapi.controller.request.auction.BidRequest;
import com.example.moduleapi.controller.request.point.PointAmount;
import com.example.moduleapi.controller.response.auction.BidResponse;
import com.example.moduleapi.exception.auction.BiddingFailException;
import com.example.moduleapi.exception.auction.RedisLockAcquisitionException;
import com.example.moduleapi.exception.auction.RedisLockInterruptedException;
import com.example.moduleapi.service.dto.BiddingSuccessfulResult;
import com.example.moduleapi.service.point.PointService;
import com.example.moduleapi.service.product.ProductFacade;
import com.example.moduledomain.common.response.ProductFindResponse;
import com.example.moduledomain.domain.bidLogging.BidLogging;
import com.example.moduledomain.domain.user.CustomUserDetails;
import com.example.moduledomain.domain.user.Gender;
import com.example.moduledomain.domain.user.User;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class AuctionService {

    private final ProductFacade productFacade;
    private final HighestBidSseNotificationService bidSseNotificationService;
    private final RedissonClient redissonClient;
    private final KafkaProducerService kafkaProducerService;
    private final BidLoggingService bidLoggingService;
    private final PointService pointService;

    private static final int FIRST_BID = -1;

    public AuctionService(ProductFacade productFacade,
                          HighestBidSseNotificationService bidSseNotificationService,
                          RedissonClient redissonClient,
                          KafkaProducerService kafkaProducerService,
                          BidLoggingService bidLoggingService,
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
        BiddingSuccessfulResult bidResult;

        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                throw new RedisLockAcquisitionException(productId);
            }
            bidResult = processBid(user, bidRequest, productId);
            kafkaProducerService.publishAuctionPriceChangeNotification(productId, bidResult.getCurrentPrice());
            bidSseNotificationService.sendToAllUsers(productId, "최고가가 " + bidResult.getCurrentPrice() + "원으로 올랐습니다.", "최고가 수정 알림");
        } catch (InterruptedException e) {
            throw new RedisLockInterruptedException(productId, e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return BidResponse.from(productId, bidResult.getRateOfIncrease());
    }

    @Transactional
    public Optional<Pair<Long, Integer>> getAuctionUserInfoByProductId(Long productId) {
        RMap<Long, Pair<Long, Integer>> highestBidMap = redissonClient.getMap(String.valueOf(productId)); // productId : (userId, bestPrice)
        Pair<Long, Integer> userIdAndCurrentPrice = highestBidMap.get(productId); // (userId, 최고가) 가져오기
        return Optional.ofNullable(userIdAndCurrentPrice); // (userId, 최고가) 가져오기
    }

    private BiddingSuccessfulResult processBid(CustomUserDetails customUserDetails, BidRequest bidRequest, Long productId) {

        RMap<Long, Pair<Long, Integer>> highestBidMap = redissonClient.getMap(String.valueOf(productId));// productId : (userId, bestPrice)
        Pair<Long, Integer> userIdAndCurrentPrice = highestBidMap.get(productId); // (userId, 최고가)
        User user = customUserDetails.getUser();

        boolean isAuctionSuccessful = isAuctionSuccessful(userIdAndCurrentPrice, bidRequest);
        BidLogging bidLogging = createBidLogging(user.getId(), productId, user.getGender(), bidRequest.getBiddingPrice(), user.getAge(), isAuctionSuccessful);
        bidLoggingService.logging(bidLogging);

        if (userIdAndCurrentPrice == null) { // 최초 입찰
            return updateRedisBidData(user, highestBidMap, bidRequest, productId);
        }
        if (bidRequest.getBiddingPrice() <= userIdAndCurrentPrice.getSecond()) {
            pointService.rollbackPoint(user.getId(), bidRequest.getBiddingPrice());
            throw new BiddingFailException(user.getUserId(), bidRequest.getBiddingPrice(), productId);
        }

        pointService.rollbackPoint(userIdAndCurrentPrice.getFirst(), userIdAndCurrentPrice.getSecond());
        return updateRedisBidData(user, highestBidMap, bidRequest, productId);
    }

    private BiddingSuccessfulResult updateRedisBidData(User user, RMap<Long, Pair<Long, Integer>> highestBidMap, BidRequest bidRequest, Long productId) {
        Pair<Long, Integer> previousMap = highestBidMap.get(productId);

        if (previousMap == null) { // 최초 입찰
            Pair<Long, Integer> newPair = Pair.of(user.getId(), bidRequest.getBiddingPrice());
            highestBidMap.put(productId, newPair); // productId에 대한 최고가 정보 업데이트
            double rateOfIncrease = calculateIncreaseRate(productId, FIRST_BID, bidRequest.getBiddingPrice());
            return BiddingSuccessfulResult.from(bidRequest.getBiddingPrice(), rateOfIncrease);
        }

        int previousPrice = previousMap.getSecond();
        double rateOfIncrease = calculateIncreaseRate(productId, previousPrice, bidRequest.getBiddingPrice());

        Pair<Long, Integer> newPair = Pair.of(user.getId(), bidRequest.getBiddingPrice());
        highestBidMap.put(productId, newPair); // productId에 대한 최고가 정보 업데이트

        return BiddingSuccessfulResult.from(bidRequest.getBiddingPrice(), rateOfIncrease);
    }

    private double calculateIncreaseRate(Long productId, int previousPrice, int nextPrice) {
        if (previousPrice == FIRST_BID) { // 최초 입찰
            ProductFindResponse product = productFacade.findById(productId);
            return increaseRate(product.getStartPrice(), nextPrice);
        }
        return increaseRate(previousPrice, nextPrice);
    }

    private double increaseRate(int previousPrice, int nextPrice) {
        double rate = ((double) (nextPrice - previousPrice) / previousPrice) * 100;
        BigDecimal rateDecimal = BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP);
        return rateDecimal.doubleValue();
    }

    private BidLogging createBidLogging(Long userId, Long productId, Gender gender, int age, int price, boolean isAuctionSuccessful) {
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

    private void isBiddingAvailable(CustomUserDetails user, BidRequest bidRequest, Long productId) {
        LocalDateTime biddingRequestTime = LocalDateTime.now();
        ProductFindResponse product = productFacade.findById(productId);
        if (biddingRequestTime.isAfter(product.getCloseDate())) {
            throw new BiddingFailException(user.getUser().getUserId(), bidRequest.getBiddingPrice(), productId);
        }

        PointAmount pointAmount = new PointAmount(bidRequest.getBiddingPrice());
        pointService.deductPoint(user, pointAmount);
    }

    private boolean isAuctionSuccessful(Pair<Long, Integer> userIdAndCurrentPrice, BidRequest bidRequest) {
        if (userIdAndCurrentPrice == null) {
            return true;
        }
        return bidRequest.getBiddingPrice() > userIdAndCurrentPrice.getSecond();
    }


}
