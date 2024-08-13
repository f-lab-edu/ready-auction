package com.example.readyauction.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String name;

    private String encodedPassword;

    @Builder
    public User(String userId, String name, String encodedPassword) {
        this.userId = userId;
        this.name = name;
        this.encodedPassword = encodedPassword;
    }

    public void updateEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }
}
