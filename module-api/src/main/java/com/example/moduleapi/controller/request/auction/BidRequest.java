package com.example.moduleapi.controller.request.auction;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BidRequest {
    private Long biddingPrice;

    public BidRequest(Long biddingPrice) {
        this.biddingPrice = biddingPrice;
    }
}
