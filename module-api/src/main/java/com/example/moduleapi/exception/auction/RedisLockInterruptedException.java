package com.example.moduleapi.exception.auction;

public class RedisLockInterruptedException extends RuntimeException {
    public RedisLockInterruptedException(Long productId, Exception e) {
        super(e.getMessage() + ": Redis에서 상품 ID : " + productId + " 처리하는 과정에서 예외 발생.");
    }
}
