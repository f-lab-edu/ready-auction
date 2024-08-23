package com.example.readyauction.domain.user;

import com.example.readyauction.domain.BaseEntity;

import jakarta.persistence.Entity;
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
