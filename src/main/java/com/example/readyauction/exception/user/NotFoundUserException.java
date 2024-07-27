package com.example.readyauction.exception.user;

public class NotFoundUserException extends RuntimeException {
	public NotFoundUserException() {
		super("존재하지 않는 회원입니다.");
	}
}
