package com.example.readyauction.exception.user;

public class DuplicatedUserIdException extends RuntimeException {
	public DuplicatedUserIdException() {
		super("이미 등록된 아이디입니다.");
	}
}
