package com.example.readyauction.exception.auction;

public class RedisLockOperationException extends RuntimeException {
    public RedisLockOperationException(Long productId, Exception e) {
        super(e.getMessage() + ": Redis에서 해당 ProductId 처리하는 과정에서 예외 발생.");
    }
}
