package com.example.moduleapi.exception.user;

public class DuplicatedUserIdException extends RuntimeException {
    public DuplicatedUserIdException(String userId) {
        super(userId + ": 이미 등록된 아이디입니다.");
    }
}
