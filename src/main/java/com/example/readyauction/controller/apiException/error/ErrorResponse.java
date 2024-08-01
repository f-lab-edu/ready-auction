package com.example.readyauction.controller.apiException.error;

import lombok.Getter;

@Getter
public class ErrorResponse {
	private String message;

	public ErrorResponse(String message) {
		this.message = message;
	}
}
