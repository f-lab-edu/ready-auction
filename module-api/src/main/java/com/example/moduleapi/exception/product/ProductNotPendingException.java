package com.example.moduleapi.exception.product;

public class ProductNotPendingException extends RuntimeException {
    public ProductNotPendingException(Long id) {
        super(id + ": 상품의 상태가 대기중이 아닙니다.");

    }
}
