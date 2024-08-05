package com.example.readyauction.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.readyauction.resolver.LoginUserArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	private final LoginUserArgumentResolver loginUserArgumentResolver;

	public WebConfig(LoginUserArgumentResolver loginUserArgumentResolver) {
		this.loginUserArgumentResolver = loginUserArgumentResolver;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(loginUserArgumentResolver);
	}

}
