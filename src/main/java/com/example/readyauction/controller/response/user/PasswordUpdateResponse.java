package com.example.readyauction.controller.response.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordUpdateResponse {
    private String userId;

    public PasswordUpdateResponse(String userId) {
        this.userId = userId;
    }

    public static PasswordUpdateResponse from(String userId) {
        return new PasswordUpdateResponse(userId);

    }
}
