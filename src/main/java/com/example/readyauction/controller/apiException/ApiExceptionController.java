package com.example.readyauction.controller.apiException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.readyauction.controller.apiException.error.ErrorResponse;
import com.example.readyauction.exception.user.DuplicatedUserIdException;
import com.example.readyauction.exception.user.LoginFailException;
import com.example.readyauction.exception.user.NotFoundUserException;

@RestControllerAdvice
public class ApiExceptionController {

	@ExceptionHandler(NotFoundUserException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse notFoundUser(NotFoundUserException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(DuplicatedUserIdException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse duplicatedUser(DuplicatedUserIdException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(LoginFailException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse loginFail(LoginFailException e) {
		return new ErrorResponse(e.getMessage());
	}

}
