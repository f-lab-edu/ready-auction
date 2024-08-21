package com.example.readyauction.service.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.readyauction.controller.request.user.PasswordUpdateRequest;
import com.example.readyauction.controller.request.user.UserSaveRequest;
import com.example.readyauction.controller.response.user.PasswordUpdateResponse;
import com.example.readyauction.controller.response.user.UserResponse;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.exception.user.DuplicatedUserIdException;
import com.example.readyauction.exception.user.NotFoundUserException;
import com.example.readyauction.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	private final LoginService loginService;

	public UserService(UserRepository userRepository,
		LoginService loginService) {
		this.userRepository = userRepository;
		this.loginService = loginService;
	}

	@Transactional
	public UserResponse join(UserSaveRequest userSaveRequest) {
		checkedDuplicatedUser(userSaveRequest.getUserId());
		User user = userSaveRequest.toEntity();

		String encodedPassword = encryptPassword(userSaveRequest.getPassword());
		user.updateEncodedPassword(encodedPassword);

		User savedUser = userRepository.save(user);
		return new UserResponse().from(savedUser);
	}

	@Transactional
	public PasswordUpdateResponse updatePassword(PasswordUpdateRequest passwordUpdateRequest, String userId) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundUserException(userId));

		String encodedPassword = encryptPassword(passwordUpdateRequest.getPassword());
		user.updateEncodedPassword(encodedPassword);

		return new PasswordUpdateResponse(user.getUserId());
	}

	@Transactional
	public User getCurrentLoginUser() {
		String userId = loginService.getCurrentLoginUserId();
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new IllegalArgumentException());
		return user;
	}

	private String encryptPassword(String password) {
		String encodedPassword = bCryptPasswordEncoder.encode(password);
		return encodedPassword;
	}

	private void checkedDuplicatedUser(String userId) {
		userRepository.findByUserId(userId).ifPresent((user) -> {
			throw new DuplicatedUserIdException(userId);
		});
	}
}
