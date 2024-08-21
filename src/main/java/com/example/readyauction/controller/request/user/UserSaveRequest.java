package com.example.readyauction.controller.request.user;

import com.example.readyauction.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSaveRequest {

    private String name;
    private String userId;
    private String password;

    @Builder
    public UserSaveRequest(String name, String userId, String password) {
        this.name = name;
        this.userId = userId;
        this.password = password;
    }

    public User toEntity() {
        return User.builder()
                .userId(userId)
                .name(name)
                .build();
    }
}
