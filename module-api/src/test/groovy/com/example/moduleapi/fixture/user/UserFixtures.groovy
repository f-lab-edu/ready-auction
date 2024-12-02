package com.example.moduleapi.fixture.user

import com.example.moduledomain.domain.user.User

class UserFixtures {
    static User createUser(Map map = [:]) {
        return User.builder()
                .userId(map.getOrDefault("userId", "test") as String)
                .name(map.getOrDefault("name", "test") as String)
                .encodedPassword(map.get("encodedPassword", "Password123!") as String)
                .build()
    }
}
