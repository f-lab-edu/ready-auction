package com.example.readyauction.controller.response.user;

import lombok.Getter;

@Getter
public class PasswordUpdateResponse {
  private String userId;

  public PasswordUpdateResponse(String userId){
    this.userId = userId;
  }

}
