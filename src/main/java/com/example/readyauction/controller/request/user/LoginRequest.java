package com.example.readyauction.controller.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

	@NotBlank(message = "아이디를 입력해주세요.")
	private String userId;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	private String password;

	public LoginRequest() {
	}

	public LoginRequest(String userId, String password) {
		this.userId = userId;
		this.password = password;
	}
}
