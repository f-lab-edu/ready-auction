package com.example.readyauction.controller.response.user;

import com.example.readyauction.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSaveResponse {
    private String userId;
    private String name;

    public UserSaveResponse(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public static UserSaveResponse from(User user) {
        return new UserSaveResponse(user.getUserId(), user.getName());
    }
}
