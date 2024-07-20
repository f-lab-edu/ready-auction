package com.example.readyauction.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String userId;
  private String name;

  @Setter
  private String encodedPassword;
  public User(){}
  @Builder
  public User(String userId, String name, String encodedPassword) {
    this.userId = userId;
    this.name = name;
    this.encodedPassword = encodedPassword;
  }
}
