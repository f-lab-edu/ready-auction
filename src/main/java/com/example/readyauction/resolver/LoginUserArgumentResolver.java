package com.example.readyauction.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.readyauction.annotaion.LoginUser;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.service.user.UserService;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
	private final UserService userService;

	public LoginUserArgumentResolver(UserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(LoginUser.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		try {
			User user = userService.getCurrentLoginUser();
			return user;
		} catch (IllegalArgumentException e) {
			throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
		}
	}
}
