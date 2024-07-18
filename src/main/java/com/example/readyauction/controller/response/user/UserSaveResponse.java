package com.example.readyauction.controller.response.user;

import com.example.readyauction.domain.user.User;
import lombok.Getter;

@Getter
public class UserSaveResponse {
  private String userId;
  private String name;
  private String address;
  public UserSaveResponse(){}
  public UserSaveResponse(String userId, String name, String address){
    this.userId = userId;
    this.name = name;
    this.address = address;
  }

  public UserSaveResponse from(User user){
    return new UserSaveResponse(user.getUserId(),user.getName(),user.getAddress());

  }
}
