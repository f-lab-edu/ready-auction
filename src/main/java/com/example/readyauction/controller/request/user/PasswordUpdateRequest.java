package com.example.readyauction.controller.request.user;

import lombok.Getter;

@Getter
public class PasswordUpdateRequest {
  private String password;

  public PasswordUpdateRequest(){}
  public PasswordUpdateRequest(String password){
    this.password = password;
  }

}
