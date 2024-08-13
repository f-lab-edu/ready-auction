package com.example.readyauction.controller.response.user;

import com.example.readyauction.domain.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponse {
	private String userId;
	private String name;

	public UserResponse(String userId, String name) {
		this.userId = userId;
		this.name = name;
	}

	public UserResponse from(User user) {
		return new UserResponse(user.getUserId(), user.getName());

	}
}
