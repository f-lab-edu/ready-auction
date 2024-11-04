package com.example.moduleapi.exception.user;

public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException(String userId) {
        super(userId + ": 사용자 유효성 검증 실패입니다.");
    }

}
