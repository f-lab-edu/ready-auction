package com.example.readyauction.service.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.readyauction.controller.request.user.LoginRequest;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.exception.user.LoginFailException;
import com.example.readyauction.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class LoginService {
	private static final String USER_ID = "USER_ID";
	private final HttpSession httpSession;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;

	public LoginService(HttpSession httpSession, UserRepository userRepository) {
		this.httpSession = httpSession;
		this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
		this.userRepository = userRepository;
	}

	@Transactional
	public void login(LoginRequest loginRequest) {
		String userId = loginRequest.getUserId();
		String password = loginRequest.getPassword();

		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new LoginFailException(userId));

		if (bCryptPasswordEncoder.matches(password, user.getEncodedPassword())) {
			httpSession.setAttribute(USER_ID, userId);
			httpSession.setMaxInactiveInterval(3600);
		} else {
			throw new LoginFailException(userId);
		}
	}

	@Transactional
	public String getCurrentLoginUserId() {
		Object userId = httpSession.getAttribute(USER_ID);
		if (userId == null) {
			return null;
		}
		return (String)userId;
	}

	@Transactional
	public void logout() {
		httpSession.invalidate();
	}

}
