package com.example.moduleapi.controller.response.user;

import com.example.moduledomain.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String userId;
    private String name;

    public UserResponse(Long id, String userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUserId(), user.getName());
    }
}
