package com.example.readyauction.service.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.readyauction.controller.request.user.LoginRequest;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.exception.user.LoginFailException;
import com.example.readyauction.repository.UserRepository;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

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
		validLoginRequest(loginRequest);

		User user = userRepository.findByUserId(loginRequest.getUserId())
			.orElseThrow(() -> new LoginFailException(loginRequest.getUserId()));

		if (bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getEncodedPassword())) {
			httpSession.setAttribute(USER_ID, loginRequest.getUserId());
			httpSession.setMaxInactiveInterval(3600);
		} else {
			throw new LoginFailException(loginRequest.getUserId());
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

	private void validLoginRequest(LoginRequest loginRequest) {
		// 검증: userId가 비어있지 않은지 확인
		Preconditions.checkArgument(!Strings.isNullOrEmpty(loginRequest.getUserId()), "아이디를 입력해주세요.");

		// 검증: password가 비어있지 않은지 확인
		Preconditions.checkArgument(!Strings.isNullOrEmpty(loginRequest.getPassword()), "비밀번호를 입력해주세요.");
	}

}
