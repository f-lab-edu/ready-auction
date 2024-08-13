package com.example.readyauction.exception.product;

public class UnauthorizedProductAccessException extends RuntimeException {
    public UnauthorizedProductAccessException(String userId, Long productId) {
        super(userId + productId + " : 상품 접근 불가능합니다.");

    }
}
