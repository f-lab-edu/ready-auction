package com.example.moduleapi.service.auction

import com.example.moduleapi.controller.request.auction.BidRequest
import com.example.moduleapi.controller.response.product.ProductFindResponse
import com.example.moduleapi.exception.auction.BiddingFailException
import com.example.moduleapi.exception.auction.RedisLockAcquisitionException
import com.example.moduleapi.fixture.product.ProductFixtures
import com.example.moduleapi.fixture.user.UserFixtures
import com.example.moduleapi.service.product.ProductFacade
import com.example.moduledomain.domain.user.CustomUserDetails
import com.example.moduledomain.domain.user.User
import org.redisson.api.RLock
import org.redisson.api.RMap
import org.redisson.api.RedissonClient
import org.springframework.data.util.Pair
import spock.lang.Specification

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class AuctionServiceTest extends Specification {
    RedissonClient redissonClient = Mock()

    ProductFacade productFacade = Mock()
    HighestBidSseNotificationService bidSseNotificationService = Mock()
    AuctionService auctionService = new AuctionService(productFacade, bidSseNotificationService, redissonClient)

    def "경매 입찰 성공"() {
        given:
        // Redisson Lock 관련
        RLock lock = Mock()
        redissonClient.getLock(_) >> lock
        lock.tryLock(5, 10, TimeUnit.SECONDS) >> true
        lock.isHeldByCurrentThread() >> true

        // 입찰 제시
        BidRequest bidRequest = new BidRequest(5000)

        // User 관련
        User user = UserFixtures.createUser()
        user.id = 1L
        CustomUserDetails customUserDetails = Mock()
        customUserDetails.getUser() >> user

        // Redisson Map 관련
        RMap<Long, Pair<Long, Long>> highestBidMap = Mock()
        redissonClient.getMap(_) >> highestBidMap

        // Product 관련
        ProductFindResponse productFindResponse = ProductFixtures.createProductFindResponse()
        productFacade.findById(1L) >> productFindResponse

        when:
        def response = auctionService.biddingPrice(customUserDetails, bidRequest, 1L)

        then:
        1 * lock.unlock()
        1 * bidSseNotificationService.sendToAllUsers(_, _, _)
        response.productId == 1L
    }

    def "경매 마감으로 인한 입찰 실패"() {
        given:
        LocalDateTime productAuctionClosedTime = LocalDateTime.of(2024, 11, 19, 12, 0, 0, 0);
        // 입찰 제시
        BidRequest bidRequest = new BidRequest(5000)
        // User 관련
        User user = UserFixtures.createUser()
        user.id = 1L
        CustomUserDetails customUserDetails = Mock()
        customUserDetails.getUser() >> user
        // Product 관련
        ProductFindResponse productFindResponse = ProductFixtures.createProductFindResponse(
                closeDate: productAuctionClosedTime
        )
        productFacade.findById(1L) >> productFindResponse

        when:
        auctionService.biddingPrice(customUserDetails, bidRequest, 1L)

        then:
        def e = thrown(BiddingFailException.class)
        e.message == String.format("입찰자: %s, 입찰가: %d, 입찰 상품: %d - 입찰 실패.", user.getUserId(), bidRequest.getBiddingPrice(), 1L)
    }

    def "분산락 획득 실패로 인한 실패"() {
        given:
        // Redisson Lock 관련
        RLock lock = Mock()
        redissonClient.getLock(_) >> lock
        lock.tryLock(5, 10, TimeUnit.SECONDS) >> false
        // 입찰 제시
        BidRequest bidRequest = new BidRequest(5000)
        // User 관련
        User user = UserFixtures.createUser()
        user.id = 1L
        CustomUserDetails customUserDetails = Mock()
        customUserDetails.getUser() >> user
        // Product 관련
        ProductFindResponse productFindResponse = ProductFixtures.createProductFindResponse()
        productFacade.findById(1L) >> productFindResponse

        when:
        auctionService.biddingPrice(customUserDetails, bidRequest, 1L)

        then:
        def e = thrown(RedisLockAcquisitionException.class)
        e.message == 1L + "Redis에서 해당 ProductId의 Lock 획득 실패."
    }

    def "최고가보다 낮은 가격 제안으로 인한 입찰 실패"() {
        given:
        // Redisson Lock 관련
        RLock lock = Mock()
        redissonClient.getLock(_) >> lock
        lock.tryLock(5, 10, TimeUnit.SECONDS) >> true
        lock.isHeldByCurrentThread() >> true

        // 입찰 제시
        BidRequest bidRequest = new BidRequest(5000)

        // User 관련
        User user = UserFixtures.createUser()
        user.id = 1L
        CustomUserDetails customUserDetails = Mock()
        customUserDetails.getUser() >> user

        // Redisson Map 관련
        RMap<Long, Pair<Long, Long>> highestBidMap = Mock()
        Pair<Long, Long> userIdAndCurrentPrice = new Pair<>(1L, 7000L)
        highestBidMap.get(1L) >> userIdAndCurrentPrice
        userIdAndCurrentPrice.getSecond() >> 7000L
        redissonClient.getMap(_) >> highestBidMap

        // Product 관련
        ProductFindResponse productFindResponse = ProductFixtures.createProductFindResponse()
        productFacade.findById(1L) >> productFindResponse

        when:
        auctionService.biddingPrice(customUserDetails, bidRequest, 1L)

        then:
        def e = thrown(BiddingFailException.class)
        e.message == String.format("입찰자: %s, 입찰가: %d, 입찰 상품: %d - 입찰 실패.", user.getUserId(), bidRequest.getBiddingPrice(), 1L)
    }
}
