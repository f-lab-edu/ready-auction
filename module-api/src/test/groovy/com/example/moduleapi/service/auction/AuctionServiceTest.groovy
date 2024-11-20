package com.example.moduleapi.service.auction

import com.example.moduleapi.controller.request.auction.BidRequest
import com.example.moduleapi.controller.response.product.ProductFindResponse
import com.example.moduleapi.fixture.UserFixtures.UserFixtures
import com.example.moduleapi.fixture.product.ProductFixtures
import com.example.moduleapi.service.product.ProductFacade
import com.example.moduledomain.domain.user.CustomUserDetails
import com.example.moduledomain.domain.user.User
import org.redisson.api.RLock
import org.redisson.api.RMap
import org.redisson.api.RedissonClient
import org.springframework.data.util.Pair
import spock.lang.Specification

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

    def "경매 입찰 실패"() {
        //
    }


}
