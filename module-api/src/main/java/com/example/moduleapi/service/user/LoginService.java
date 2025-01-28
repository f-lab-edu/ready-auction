package com.example.moduleapi.service.user;

import com.example.moduleapi.controller.request.user.LoginRequest;
import com.example.moduleapi.exception.user.LoginFailException;
import com.example.moduleapi.exception.user.NotFoundUserException;
import com.example.moduledomain.domain.user.CustomUserDetails;
import com.example.moduledomain.domain.user.User;
import com.example.moduledomain.repository.user.UserRepository;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LoginService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public LoginService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public String login(LoginRequest loginRequest) {
        validLoginRequest(loginRequest);
        User user = userRepository.findByUserId(loginRequest.getUserId()).orElseThrow(() -> new LoginFailException(loginRequest.getUserId()));

        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getEncodedPassword())) {
            throw new LoginFailException(loginRequest.getUserId());
        }

        return loginRequest.getUserId();
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserId(userId);
        if (!user.isPresent()) {
            throw new NotFoundUserException(userId);
        }

        return new CustomUserDetails(user.get());
    }

    private void validLoginRequest(LoginRequest loginRequest) {
        // 검증: userId가 비어있지 않은지 확인
        Preconditions.checkArgument(!Strings.isNullOrEmpty(loginRequest.getUserId()), "아이디를 입력해주세요.");
        // 검증: password가 비어있지 않은지 확인
        Preconditions.checkArgument(!Strings.isNullOrEmpty(loginRequest.getPassword()), "비밀번호를 입력해주세요.");
    }
}
