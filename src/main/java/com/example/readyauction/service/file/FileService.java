package com.example.readyauction.service.file;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.readyauction.controller.response.ImageResponse;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductImage;

public interface FileService {
	ProductImage uploadImage(String userId, MultipartFile file);

	List<ProductImage> uploadImages(String userId, List<MultipartFile> files);

	List<ImageResponse> loadImages(Product product);

	void deleteImagesLocal(List<ImageResponse> imageResponses); // 로컬에서 이미지 삭제

	void deleteImagesDB(Product product); // DB에서 삭제

}
