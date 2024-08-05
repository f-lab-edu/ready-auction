package com.example.readyauction.controller.response.product;

import java.time.LocalDateTime;

import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.Status;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductSaveResponse {
	private Long productId;
	private String productName;
	private String description;
	private LocalDateTime startDate;
	private LocalDateTime closeDate;
	private Status status;
	private int startPrice;

	public ProductSaveResponse(Long productId, String productName, String description, LocalDateTime startDate,
		LocalDateTime closeDate, Status status, int startPrice) {
		this.productId = productId;
		this.productName = productName;
		this.description = description;
		this.startDate = startDate;
		this.closeDate = closeDate;
		this.status = status;
		this.startPrice = startPrice;
	}

	public ProductSaveResponse from(Product product) {
		return new ProductSaveResponse(
			product.getId(),
			product.getProductName(),
			product.getDescription(),
			product.getStartDate(),
			product.getCloseDate(),
			product.getStatus(),
			product.getStartPrice()
		);
	}
}
