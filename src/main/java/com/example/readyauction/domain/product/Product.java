package com.example.readyauction.domain.product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.readyauction.domain.user.User;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
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
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "Product")
public class Product {
	@Builder
	public Product(User user, String name, String description, LocalDateTime startDate, LocalDateTime closeDate,
		int startPrice) {
		this.user = user;
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.closeDate = closeDate;
		this.startPrice = startPrice;
		this.status = Status.PENDING;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@Column(name = "product_name", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable
	@OrderColumn
	@Column(name = "imagePaths", nullable = false)
	private List<String> imagePaths = new ArrayList<>();

	@Column(name = "startDate", nullable = false)
	private LocalDateTime startDate;

	@Column(name = "closeDate", nullable = false)
	private LocalDateTime closeDate;

	@Column(name = "startPrice", nullable = false)
	private int startPrice;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;

	public void updateProductInfo(String name, String description, List<String> imagePaths, LocalDateTime startDate,
		LocalDateTime closeDate, int startPrice) {
		this.name = name;
		this.description = description;
		this.imagePaths = imagePaths;
		this.startDate = startDate;
		this.closeDate = closeDate;
		this.startPrice = startPrice;
	}

}
