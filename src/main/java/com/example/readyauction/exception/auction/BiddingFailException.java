package com.example.readyauction.exception.auction;

public class BiddingFailException extends RuntimeException {
    public BiddingFailException(Long productId) {
        super(productId + ": 입찰 실패.");

    }
}
