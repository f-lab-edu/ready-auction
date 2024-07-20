package com.example.readyauction.controller.response.user;

import com.example.readyauction.domain.user.User;

import lombok.Getter;

@Getter
public class UserSaveResponse {
	private String userId;
	private String name;

	public UserSaveResponse() {
	}

	public UserSaveResponse(String userId, String name) {
		this.userId = userId;
		this.name = name;
	}

	public UserSaveResponse from(User user) {
		return new UserSaveResponse(user.getUserId(), user.getName());

	}
}
