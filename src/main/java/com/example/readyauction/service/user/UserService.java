package com.example.readyauction.service.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.example.readyauction.controller.request.user.PasswordUpdateRequest;
import com.example.readyauction.controller.request.user.UserSaveRequest;
import com.example.readyauction.controller.response.user.PasswordUpdateResponse;
import com.example.readyauction.controller.response.user.UserResponse;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.exception.user.DuplicatedUserIdException;
import com.example.readyauction.exception.user.NotFoundUserException;
import com.example.readyauction.exception.user.UnauthorizedUserException;
import com.example.readyauction.repository.user.UserRepository;
import com.google.common.base.Preconditions;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public UserResponse join(UserSaveRequest userSaveRequest) {
        validUserSaveRequest(userSaveRequest);
        checkedDuplicatedUser(userSaveRequest.getUserId());
        User user = userSaveRequest.toEntity();

        String encodedPassword = encryptPassword(userSaveRequest.getPassword());
        user.updateEncodedPassword(encodedPassword);

        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    @Transactional
    public PasswordUpdateResponse updatePassword(UserDetails userDetails, PasswordUpdateRequest passwordUpdateRequest,
        String userId) {
        validateUser(userDetails, userId);
        validUpdatePasswordRequest(passwordUpdateRequest);
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundUserException(userId));

        String encodedPassword = encryptPassword(passwordUpdateRequest.getPassword());
        user.updateEncodedPassword(encodedPassword);

        return PasswordUpdateResponse.from(user.getUserId());
    }

    private void validateUser(UserDetails userDetails, String userId) {
        if (!userId.equals(userDetails.getUsername())) {
            throw new UnauthorizedUserException(userId);
        }
    }

    private void validUserSaveRequest(UserSaveRequest userSaveRequest) {
        // 검증: 이름이 비어있지 않은지 확인
        Preconditions.checkArgument(!ObjectUtils.isEmpty(userSaveRequest.getName()), "이름을 입력해주세요.");

        // 검증: 아이디가 비어있지 않고 길이가 6~10자인지 확인
        Preconditions.checkArgument(!ObjectUtils.isEmpty(userSaveRequest.getUserId()), "아이디를 입력해주세요.");
        Preconditions.checkArgument(
            userSaveRequest.getUserId().length() >= 6 && userSaveRequest.getUserId().length() <= 10,
            "아이디는 6자 이상 10자 이하로 입력해주세요.");

        // 검증: 비밀번호가 비어있지 않고 규칙에 맞는지 확인
        Preconditions.checkArgument(!ObjectUtils.isEmpty(userSaveRequest.getPassword()), "비밀번호를 입력해주세요.");
        Preconditions.checkArgument(
            userSaveRequest.getPassword().length() >= 8 && userSaveRequest.getPassword().length() <= 15,
            "비밀번호는 8자 이상 15자 이하로 입력해주세요.");

        // 정규 표현식으로 비밀번호 검증 (하드코딩된 비밀번호 아님)
        String regex = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,15}";
        Preconditions.checkArgument(userSaveRequest.getPassword().matches(regex),
            "비밀번호는 8~15자 길이여야 하며, 최소 1개의 영문 대소문자, 숫자, 그리고 특수문자를 포함해야 합니다.");
    }

    private void validUpdatePasswordRequest(PasswordUpdateRequest passwordUpdateRequest) {
        // 검증: 비밀번호가 비어있지 않고 규칙에 맞는지 확인
        Preconditions.checkArgument(!ObjectUtils.isEmpty(passwordUpdateRequest.getPassword()), "비밀번호를 입력해주세요.");
        Preconditions.checkArgument(
            passwordUpdateRequest.getPassword().length() >= 8 && passwordUpdateRequest.getPassword().length() <= 15,
            "비밀번호는 8자 이상 15자 이하로 입력해주세요.");

        // 정규 표현식으로 비밀번호 검증
        String regex = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,15}";
        Preconditions.checkArgument(passwordUpdateRequest.getPassword().matches(regex),
            "비밀번호는 8~15자 길이여야 하며, 최소 1개의 영문 대소문자, 숫자, 그리고 특수문자를 포함해야 합니다.");
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
