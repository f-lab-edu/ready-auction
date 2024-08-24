package com.example.readyauction.domain.user;

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
public class User {

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
}
