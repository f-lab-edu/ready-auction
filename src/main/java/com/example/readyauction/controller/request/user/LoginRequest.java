package com.example.readyauction.controller.request.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    private String userId;
    private String password;

    public LoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
