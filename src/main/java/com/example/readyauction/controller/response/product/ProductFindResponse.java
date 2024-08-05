package com.example.readyauction.controller.response.product;

import java.time.LocalDateTime;
import java.util.List;

import com.example.readyauction.controller.response.ImageResponse;
import com.example.readyauction.controller.response.user.UserResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductFindResponse {
	private UserResponse userResponse;
	private List<ImageResponse> imagePath;
	private String productName;
	private String description;
	private LocalDateTime startDate;
	private LocalDateTime closeDate;
	private int startPrice;

	public ProductFindResponse(UserResponse userResponse, List<ImageResponse> imagePath, String productName,
		String description,
		LocalDateTime startDate, LocalDateTime closeDate, int startPrice) {
		this.userResponse = userResponse;
		this.imagePath = imagePath;
		this.productName = productName;
		this.description = description;
		this.startDate = startDate;
		this.closeDate = closeDate;
		this.startPrice = startPrice;
	}

	public ProductFindResponse from(UserResponse userResponse, List<ImageResponse> imagePath,
		ProductSaveResponse productSaveResponse) {
		return new ProductFindResponse(userResponse, imagePath, productSaveResponse.getProductName(),
			productSaveResponse.getDescription(),
			productSaveResponse.getStartDate(), productSaveResponse.getCloseDate(),
			productSaveResponse.getStartPrice());

	}
}
