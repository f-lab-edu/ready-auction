package com.example.moduleapi.fixture;

import com.example.moduleapi.controller.request.point.PointAmount;
import com.example.moduleapi.controller.response.point.PointResponse;

public class PointFixtures {

    public static PointAmount 포인트_충전 = PointAmount.builder()
                                                  .amount(100000L)
                                                  .build();

    public static PointResponse 포인트_응답(Long chargedAmount, Long currentBalance) {
        String message = String.format("포인트 %d원 충전 완료. [현재 포인트 잔액 : %d원]", chargedAmount, currentBalance);
        return PointResponse.from(currentBalance, message);
    }
}
