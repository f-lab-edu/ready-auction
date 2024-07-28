package com.example.readyauction.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.readyauction.controller.user.error.ErrorResponse;
import com.example.readyauction.exception.user.DuplicatedUserIdException;
import com.example.readyauction.exception.user.LoginFailException;
import com.example.readyauction.exception.user.NotFoundUserException;

@RestControllerAdvice
public class UserExceptionApiController {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse validFailException(MethodArgumentNotValidException e) {
		return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
			e.getBindingResult().getFieldError().getDefaultMessage());

	}

	@ExceptionHandler(NotFoundUserException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse notFoundUser(NotFoundUserException e) {
		return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
	}

	@ExceptionHandler(DuplicatedUserIdException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse duplicatedUser(DuplicatedUserIdException e) {
		return new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
	}

	@ExceptionHandler(LoginFailException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse loginFail(LoginFailException e) {
		return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

}
