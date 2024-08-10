package com.example.readyauction.controller.response.product;

import java.time.LocalDateTime;
import java.util.List;

import com.example.readyauction.controller.response.ImageResponse;
import com.example.readyauction.controller.response.user.UserResponse;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.user.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductFindResponse {
	private UserResponse userResponse;
	private List<ImageResponse> imageResponses;
	private String productName;
	private String description;
	private LocalDateTime startDate;
	private LocalDateTime closeDate;
	private int startPrice;

	@Builder
	public ProductFindResponse(User user, List<ImageResponse> imageResponses, String productName,
		String description, LocalDateTime startDate, LocalDateTime closeDate, int startPrice) {
		this.userResponse = new UserResponse().from(user);
		this.imageResponses = imageResponses;
		this.productName = productName;
		this.description = description;
		this.startDate = startDate;
		this.closeDate = closeDate;
		this.startPrice = startPrice;
	}

	public ProductFindResponse from(Product product, List<ImageResponse> imageResponses) {
		return ProductFindResponse.builder()
			.user(product.getUser())
			.imageResponses(imageResponses)
			.productName(product.getProductName())
			.description(product.getDescription())
			.startDate(product.getStartDate())
			.closeDate(product.getCloseDate())
			.startPrice(product.getStartPrice())
			.build();

	}
}
