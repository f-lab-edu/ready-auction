package com.example.moduleapi.controller.request.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordUpdateRequest {
    private String password;

    public PasswordUpdateRequest(String password) {
        this.password = password;
    }
}
