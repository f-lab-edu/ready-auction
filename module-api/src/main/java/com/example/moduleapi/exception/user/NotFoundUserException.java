package com.example.moduleapi.exception.user;

public class NotFoundUserException extends RuntimeException {
    public NotFoundUserException(String userId) {
        super(userId + ": 존재하지 않는 회원입니다.");
    }
}
