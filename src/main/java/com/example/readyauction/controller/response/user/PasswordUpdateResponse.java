package com.example.readyauction.controller.response.user;

import lombok.Getter;

@Getter
public class PasswordUpdateResponse {
  private String userId;
  private String msg;

  public PasswordUpdateResponse(String userId, String msg){
    this.userId = userId;
    this.msg = msg;
  }

}
