package com.example.readyauction.controller.response;

import com.example.readyauction.domain.product.ProductImage;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImageResponse {
	private String originalName;
	private String imagePath;

	public ImageResponse(String originalName, String imagePath) {
		this.originalName = originalName;
		this.imagePath = imagePath;
	}

	public static ImageResponse from(ProductImage productImage) {
		return new ImageResponse(productImage.getOriginalName(), productImage.getImageFullPath());
	}
}
