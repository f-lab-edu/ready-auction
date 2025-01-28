package com.example.moduleapi.exception.auction;

public class BiddingFailException extends RuntimeException {
    public BiddingFailException(String userId, Long price, Long productId) {
        super(String.format("입찰자: %s, 입찰가: %d, 입찰 상품: %d - 입찰 실패.", userId, price, productId));
    }
}
