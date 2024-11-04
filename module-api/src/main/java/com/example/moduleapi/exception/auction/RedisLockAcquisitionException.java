package com.example.moduleapi.exception.auction;

public class RedisLockAcquisitionException extends RuntimeException {
    public RedisLockAcquisitionException(Long productId) {
        super(productId + "Redis에서 해당 ProductId의 Lock 획득 실패.");
    }
}
