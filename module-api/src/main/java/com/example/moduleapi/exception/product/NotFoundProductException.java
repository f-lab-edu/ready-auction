package com.example.moduleapi.exception.product;

public class NotFoundProductException extends RuntimeException {
    public NotFoundProductException(Long id) {
        super(id + ": 존재하지 않는 상품입니다.");

    }
}
