package com.example.readyauction.service;

import com.example.readyauction.controller.request.user.UserSaveRequest;
import com.example.readyauction.controller.response.user.UserSaveResponse;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    return UserSaveResponse.from(savedUser); // 밖 -> 안
  }
}
