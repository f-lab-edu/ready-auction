package com.example.moduleapi.controller.request.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    private String userId;
    private String password;

    @Builder
    public LoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
