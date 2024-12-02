package com.example.moduledomain.domain.user;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("남성"),
    FEMALE("여성");
    private final String description;

    Gender(String description) {
        this.description = description;
    }
}
