package com.example.readyauction.exception.product;

public class UnauthorizedEnrollException extends RuntimeException {
    public UnauthorizedEnrollException(String userId) {
        super(userId + " : 등록 권한이 없습니다. 사용자가 일치하지 않습니다.");
    }
}
