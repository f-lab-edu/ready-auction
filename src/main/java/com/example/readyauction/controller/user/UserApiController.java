package com.example.readyauction.controller.user;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.readyauction.controller.request.user.LoginRequest;
import com.example.readyauction.controller.request.user.PasswordUpdateRequest;
import com.example.readyauction.controller.request.user.UserSaveRequest;
import com.example.readyauction.controller.response.user.PasswordUpdateResponse;
import com.example.readyauction.controller.response.user.UserResponse;
import com.example.readyauction.service.user.LoginService;
import com.example.readyauction.service.user.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1")
public class UserApiController {
	private final HttpSession session;
	private final UserService userService;
	private final LoginService loginService;

	public UserApiController(HttpSession session, UserService userService, LoginService loginService) {
		this.session = session;
		this.userService = userService;
		this.loginService = loginService;
	}

	@PostMapping("/users")
	public UserResponse join(@RequestBody UserSaveRequest userSaveRequest) {
		UserResponse userResponse = userService.join(userSaveRequest);
		return userResponse;
	}

	@PutMapping("/users/{userId}/changePassword")
	public PasswordUpdateResponse update(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody PasswordUpdateRequest passwordUpdateRequest,
		@PathVariable String userId) {
		PasswordUpdateResponse passwordUpdateResponse = userService.updatePassword(userDetails, passwordUpdateRequest,
			userId);
		return passwordUpdateResponse;
	}

	@PostMapping("/login")
	public void login(@RequestBody LoginRequest loginRequest) {
		String userId = loginService.login(loginRequest);
		UserDetails userDetails = loginService.loadUserByUsername(String.valueOf(userId));
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "",
			userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
			SecurityContextHolder.getContext());
		session.setMaxInactiveInterval(3600);
	}

	@PostMapping("/logout")
	public void logout() {
		session.invalidate();
	}
}
