package com.example.readyauction.domain.product;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.readyauction.domain.user.User;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private String name;
	private String description;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable
	@OrderColumn
	private List<String> imagePaths = new ArrayList<>();

	private ZonedDateTime startDate;
	private ZonedDateTime endDate;

	private int startPrice;

	@Enumerated(EnumType.STRING)
	private Status status;

	public void updateProductInfo(String name, String description, List<String> imagePaths, ZonedDateTime startDate,
		ZonedDateTime endDate, int startPrice) {
		this.name = name;
		this.description = description;
		this.imagePaths = imagePaths;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startPrice = startPrice;
	}

	@Builder
	public Product(User user, String name, String description, ZonedDateTime startDate, ZonedDateTime endDate,
		int startPrice) {
		this.user = user;
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startPrice = startPrice;
		this.status = Status.PENDING;
	}
}
