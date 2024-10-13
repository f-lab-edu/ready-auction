package com.example.readyauction.controller.request.auction;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BidRequest {
    private int biddingPrice; // 하나밖에 없는데 그냥 FormData로 받는게 나을듯..?
}
