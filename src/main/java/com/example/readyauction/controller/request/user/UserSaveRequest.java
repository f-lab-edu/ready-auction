package com.example.readyauction.controller.request.user;

import com.example.readyauction.controller.response.user.UserSaveResponse;
import com.example.readyauction.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserSaveRequest {
  private String name;
  private String userId;
  private String password;
  private String address;

  public UserSaveRequest() {}

  public UserSaveRequest(String name, String userId, String password, String address){
    this.name = name;
    this.userId = userId;
    this.password = password;
    this.address = address;

  }
  public User toEntity(){
    return User.builder()
        .userId(userId)
        .name(name)
        .address(address)
        .build();
  }
}
