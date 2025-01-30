package com.example.moduleapi.service.point

import com.example.moduleapi.controller.request.point.PointAmount
import com.example.moduleapi.exception.point.PointDeductionFailedException
import com.example.moduleapi.fixture.point.PointFixtures
import com.example.moduleapi.fixture.user.UserFixtures
import com.example.moduledomain.domain.point.Point
import com.example.moduledomain.domain.user.CustomUserDetails
import com.example.moduledomain.domain.user.User
import com.example.moduledomain.repository.user.PointRepository
import spock.lang.Specification

class PointServiceTest extends Specification {
    PointRepository pointRepository = Mock()
    PointService pointService = new PointService(pointRepository)

    def "포인트 충전"() {
        given:
        User user = UserFixtures.createUser()
        user.id = 1L
        CustomUserDetails customUserDetails = Mock()
        customUserDetails.getUser() >> user

        PointAmount pointAmount = new PointAmount(10000)
        Point point = PointFixtures.createPoint()
        point.amount = 150000
        pointRepository.findByUserId(1L) >> point

        when:
        def response = pointService.chargePoint(customUserDetails, pointAmount)

        then:
        response.currentPoint == 160000
        response.message == "포인트 10000원 충전 완료. [현재 포인트 잔액 : 160000원]"
    }

    def "포인트 사용"() {
        given:
        User user = UserFixtures.createUser()
        user.id = 1L
        CustomUserDetails customUserDetails = Mock()
        customUserDetails.getUser() >> user

        PointAmount pointAmount = new PointAmount(10000)
        Point point = PointFixtures.createPoint([amount: 100000])
        pointRepository.findByUserId(1L) >> point

        when:
        def response = pointService.deductPoint(customUserDetails, pointAmount)

        then:
        response == 90000
        point.getAmount() == 90000
    }

    def "잔액 부족으로 인한 포인트 사용 실패"() {
        given:
        User user = UserFixtures.createUser()
        user.id = 1L
        CustomUserDetails customUserDetails = Mock()
        customUserDetails.getUser() >> user

        PointAmount pointAmount = new PointAmount(10000)
        Point point = PointFixtures.createPoint([amount: 5000])
        pointRepository.findByUserId(1L) >> point

        when:
        pointService.deductPoint(customUserDetails, pointAmount)

        then:
        def e = thrown(PointDeductionFailedException)
        e.message == 1L + ": 포인트가 부족합니다."

    }
}
