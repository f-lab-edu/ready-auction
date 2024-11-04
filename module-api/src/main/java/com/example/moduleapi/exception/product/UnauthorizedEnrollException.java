package com.example.moduleapi.exception.product;

public class UnauthorizedEnrollException extends RuntimeException {
    public UnauthorizedEnrollException(String userId) {
        super(userId + " : 사용자 유효성 검증에 실패했습니다.");
    }
}
