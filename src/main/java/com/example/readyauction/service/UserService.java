package com.example.readyauction.service;

import com.example.readyauction.controller.request.user.UserSaveRequest;
import com.example.readyauction.controller.request.user.PasswordUpdateRequest;
import com.example.readyauction.controller.response.user.UserSaveResponse;
import com.example.readyauction.controller.response.user.PasswordUpdateResponse;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;


  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
    this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
  }

  @Transactional
  public UserSaveResponse join(UserSaveRequest userSaveRequest){
    User user = userSaveRequest.toEntity();

    String encodedPassword = bCryptPasswordEncoder.encode(userSaveRequest.getPassword());
    user.setEncodedPassword(encodedPassword);

    User savedUser = userRepository.save(user);
    UserSaveResponse userSaveResponse = new UserSaveResponse();
    return userSaveResponse.from(savedUser); // 밖 -> 안
  }

  // 회원정보 수정
  @Transactional
  public PasswordUpdateResponse updatePassword(PasswordUpdateRequest passwordUpdateRequest, String userId) {
    Optional<User> user = userRepository.findByUserId(userId);
    User findUser = user.get();

    if (findUser == null){
      throw new IllegalArgumentException("존재하지 않는 회원입니다.");
    }

    String encodedPassword = bCryptPasswordEncoder.encode(passwordUpdateRequest.getPassword());
    findUser.setEncodedPassword(encodedPassword);

    return new PasswordUpdateResponse(findUser.getUserId());
  }
}
