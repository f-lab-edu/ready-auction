package com.example.moduleapi.service.auction

import com.example.moduledomain.domain.bidLogging.BidLogging
import com.example.moduledomain.domain.product.Category
import com.example.moduledomain.domain.user.Gender
import com.example.moduledomain.repository.bidLogging.BidLoggingRepository
import spock.lang.Specification

class BidLoggingServiceTest extends Specification {
    BidLoggingRepository bidLoggingRepository = Mock()
    BidLoggingService bidLoggingService = new BidLoggingService(bidLoggingRepository)

    def "입찰 로깅 성공"() {
        given:
        BidLogging bidLogging = new BidLogging(
                userId: 1L,
                productId: 1L,
                category: Category.ELECTRONICS,
                gender: Gender.FEMALE,
                age: 24,
                isAuctionSuccessful: true
        )
        when:
        bidLoggingService.logging(bidLogging)

        then:

        1 * bidLoggingRepository.save(_) >> {
            BidLogging logging = it[0]
            verifyAll(logging) {
                assert it.userId == 1L
                assert it.productId == 1L
                assert it.category == Category.ELECTRONICS
                assert it.gender == Gender.FEMALE
                assert it.age == 24
                assert it.isAuctionSuccessful == true
            }
        }
    }
}
