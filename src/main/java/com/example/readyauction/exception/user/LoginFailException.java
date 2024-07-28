package com.example.readyauction.exception.user;

public class LoginFailException extends RuntimeException {
	public LoginFailException(String userId) {
		super(userId + ": 로그인 실패했습니다.");
	}
}
