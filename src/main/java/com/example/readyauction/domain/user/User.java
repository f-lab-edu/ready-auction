package com.example.readyauction.domain.user;

import java.util.Objects;

import com.example.readyauction.domain.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String name;
    private String encodedPassword;
    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;

    @Builder
    public User(String userId, String name, String encodedPassword) {
        this.userId = userId;
        this.name = name;
        this.encodedPassword = encodedPassword;
    }

    public void updateEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User)o;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getUserId(), user.getUserId())
            && Objects.equals(getName(), user.getName()) && Objects.equals(getEncodedPassword(),
            user.getEncodedPassword()) && getRole() == user.getRole();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUserId(), getName(), getEncodedPassword(), getRole());
    }
}
