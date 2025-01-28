package com.example.moduleapi.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BiddingSuccessfulResult {
    private Long currentPrice;
    private double rateOfIncrease;

    @Builder
    public BiddingSuccessfulResult(Long currentPrice, double rateOfIncrease) {
        this.currentPrice = currentPrice;
        this.rateOfIncrease = rateOfIncrease;
    }

    public static BiddingSuccessfulResult from(Long currentPrice, double rateOfIncrease) {
        return BiddingSuccessfulResult.builder()
                                      .currentPrice(currentPrice)
                                      .rateOfIncrease(rateOfIncrease)
                                      .build();
    }
}
