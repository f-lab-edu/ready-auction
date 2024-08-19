package com.example.readyauction.exception.product;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String userId) {
        super(userId + " : 사용자 유효성 검증에 실패했습니다.");
    }
}
