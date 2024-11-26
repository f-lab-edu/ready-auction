package com.example.moduleapi.controller.request.user;

import com.example.moduledomain.domain.user.Gender;
import com.example.moduledomain.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UserSaveRequest {

    private String name;
    private String userId;
    private String password;
    private LocalDate birthDate;
    private Gender gender;

    @Builder
    public UserSaveRequest(String name, String userId, String password, LocalDate birthDate, Gender gender) {
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public User toEntity() {
        return User.builder()
                .userId(userId)
                .name(name)
                .birthDate(birthDate)
                .gender(gender)
                .build();
    }
}
