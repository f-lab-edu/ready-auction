package com.example.readyauction.controller.response.auction;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TenderResponse {
    private Long productId;
    private double rateOfIncrease;

    @Builder
    public TenderResponse(Long productId, double rateOfIncrease) {
        this.productId = productId;
        this.rateOfIncrease = rateOfIncrease;
    }

}
