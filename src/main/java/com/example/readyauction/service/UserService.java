package com.example.readyauction.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.readyauction.controller.request.user.PasswordUpdateRequest;
import com.example.readyauction.controller.request.user.UserSaveRequest;
import com.example.readyauction.controller.response.user.PasswordUpdateResponse;
import com.example.readyauction.controller.response.user.UserSaveResponse;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
	}

	@Transactional
	public UserSaveResponse join(UserSaveRequest userSaveRequest) {
		User user = userSaveRequest.toEntity();

		String encodedPassword = bCryptPasswordEncoder.encode(userSaveRequest.getPassword());
		user.setEncodedPassword(encodedPassword);

		User savedUser = userRepository.save(user);
		return new UserSaveResponse().from(savedUser);
	}

	// 비밀번호 수정
	@Transactional
	public PasswordUpdateResponse updatePassword(PasswordUpdateRequest passwordUpdateRequest, String userId) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

		String encodedPassword = bCryptPasswordEncoder.encode(passwordUpdateRequest.getPassword());
		user.setEncodedPassword(encodedPassword);

		return new PasswordUpdateResponse(user.getUserId());
	}
}
