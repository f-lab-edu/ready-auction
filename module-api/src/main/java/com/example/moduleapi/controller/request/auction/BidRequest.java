package com.example.moduleapi.controller.request.auction;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BidRequest {
    private int biddingPrice;

    public BidRequest(int biddingPrice) {
        this.biddingPrice = biddingPrice;
    }
}
