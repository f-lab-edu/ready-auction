package com.example.readyauction.exception.auction;

public class BiddingFailException extends RuntimeException {
    public BiddingFailException(Long productId) {
        super(productId + ": 최고가 변경으로 인한 입찰 실패.");

    }
}
