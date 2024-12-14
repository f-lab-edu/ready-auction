package com.example.modulerecommendation.service

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

    def "남성 - 나이대별 Top2 카테고리 조회"() {
        given:
        def maleCategories =
                [10: [Category.FASHION, Category.CAR],
                 20: [Category.ELECTRONICS, Category.CAR],
                 30: [Category.BEAUTY, Category.CAR],
                 40: [Category.ELECTRONICS, Category.CAR],
                 50: [Category.OTHER, Category.BOOKS],
                 60: [Category.BOOKS, Category.FURNITURE]
                ]

        when:
        def response = bidLoggingService.getCategoryTop2(Gender.MALE)

        then:
        1 * bidLoggingRepository.findTopCategoriesByGenderAndAgeRange(Gender.MALE, _) >> {
            return maleCategories
        }
        response.get("10") == maleCategories.get("10")
        response.get("20") == maleCategories.get("20")
        response.get("30") == maleCategories.get("30")
        response.get("40") == maleCategories.get("40")
        response.get("50") == maleCategories.get("50")
        response.get("60") == maleCategories.get("60")
    }

    def "여성 - 나이대별 Top2 카테고리 조회"() {
        given:
        def femaleCategories =
                [10: [Category.FASHION, Category.BEAUTY],
                 20: [Category.FASHION, Category.BEAUTY],
                 30: [Category.BEAUTY, Category.FURNITURE],
                 40: [Category.FASHION, Category.FURNITURE],
                 50: [Category.BOOKS, Category.FURNITURE],
                 60: [Category.OTHER, Category.BOOKS]
                ]

        when:
        def response = bidLoggingService.getCategoryTop2(Gender.FEMALE)

        then:
        then:
        1 * bidLoggingRepository.findTopCategoriesByGenderAndAgeRange(Gender.FEMALE, _) >> {
            return femaleCategories
        }
        response.get("10") == femaleCategories.get("10")
        response.get("20") == femaleCategories.get("20")
        response.get("30") == femaleCategories.get("30")
        response.get("40") == femaleCategories.get("40")
        response.get("50") == femaleCategories.get("50")
        response.get("60") == femaleCategories.get("60")
    }
}
