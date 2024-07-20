package com.example.readyauction.controller.request.user;

import com.example.readyauction.domain.user.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSaveRequest {

	@NotBlank(message = "이름을 입력해주세요.")
	private String name;

	@NotBlank(message = "아이디를 입력해주세요.")
	@Size(min = 6, max = 10, message = "아이디는 6자 이상 10자 이하로 입력해주세요.")
	private String userId;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,15}",
		message = "영문 대소문자와 숫자, 특수기호가 1개씩 포함되어있는 8~15자 비밀번호입니다")
	private String password;

	public UserSaveRequest() {
	}

	@Builder
	public UserSaveRequest(String name, String userId, String password) {
		this.name = name;
		this.userId = userId;
		this.password = password;
	}

	public User toEntity() {
		return User.builder()
			.userId(userId)
			.name(name)
			.build();
	}
}
