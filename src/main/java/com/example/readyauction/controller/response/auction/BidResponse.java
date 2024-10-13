package com.example.readyauction.controller.response.auction;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BidResponse {
    private Long productId;
    private double rateOfIncrease;

    @Builder
    public BidResponse(Long productId, double rateOfIncrease) {
        this.productId = productId;
        this.rateOfIncrease = rateOfIncrease;
    }

    public static BidResponse from(Long productId, double rateOfIncrease) {
        return BidResponse.builder()
            .productId(productId)
            .rateOfIncrease(rateOfIncrease)
            .build();
    }

}
