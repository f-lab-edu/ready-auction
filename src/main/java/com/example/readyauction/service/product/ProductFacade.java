package com.example.readyauction.service.product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.readyauction.controller.request.product.ProductSaveRequest;
import com.example.readyauction.controller.request.product.ProductUpdateRequest;
import com.example.readyauction.controller.response.ImageResponse;
import com.example.readyauction.controller.response.product.ProductCursorResponse;
import com.example.readyauction.controller.response.product.ProductFindResponse;
import com.example.readyauction.controller.response.product.ProductResponse;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductImage;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.exception.product.UnauthorizedEnrollException;
import com.example.readyauction.service.file.FileService;
import com.google.common.base.Preconditions;

@Service
public class ProductFacade {

	private final FileService fileService;
	private final ProductImageService productImageService;
	private final ProductService productService;

	public ProductFacade(FileService fileService, ProductImageService productImageService,
		ProductService productService) {
		this.fileService = fileService;
		this.productImageService = productImageService;
		this.productService = productService;
	}

	@Transactional
	public ProductResponse enroll(User user, ProductSaveRequest productSaveRequest, List<MultipartFile> images) {
		validateUser(user, productSaveRequest);
		validateSaveRequest(productSaveRequest, images);

		Product product = productService.enroll(productSaveRequest);
		List<ProductImage> productImages = fileService.uploadImages(user, product, images);
		productImageService.saveImage(productImages);
		return ProductResponse.from(product.getId());
	}

	@Transactional
	public ProductFindResponse findById(Long productId) {
		Product product = productService.findById(productId);
		List<ProductImage> productImages = productImageService.getImage(productId);
		List<ImageResponse> imageResponses = fileService.loadImages(productImages);
		return ProductFindResponse.from(product, imageResponses);
	}

	@Transactional
	public ProductCursorResponse findAll(Long cursorId, String sortedBy, int size) {
		List<Product> products = productService.findAll(cursorId, sortedBy, size);
		List<ProductFindResponse> productFindResponses = products.stream()
			.map(product -> {
				List<ProductImage> productImages = productImageService.getImage(product.getId());
				List<ImageResponse> imageResponses = fileService.loadImages(productImages);
				return ProductFindResponse.from(product, imageResponses);
			})
			.collect(Collectors.toList());

		boolean hasNext = products.size() == size;
		Long nextCursorId = hasNext ? products.get(products.size() - 1).getId() : -1;
		return ProductCursorResponse.from(productFindResponses, nextCursorId, hasNext);
	}

	@Transactional
	public ProductResponse update(User user, Long productId, ProductUpdateRequest productUpdateRequest,
		List<MultipartFile> images) {
		validUpdateRequest(productUpdateRequest);
		Product product = productService.update(user, productId, productUpdateRequest);
		List<ProductImage> productImages = productImageService.getImage(productId);
		List<ProductImage> newProductImages = fileService.updateImages(user, product, productImages, images);
		productImageService.updateImage(productId, newProductImages);
		return ProductResponse.from(productId);
	}

	@Transactional
	public ProductResponse delete(User user, Long productId) {
		productService.delete(user, productId);
		List<ProductImage> productImages = productImageService.deleteImage(productId);
		fileService.deleteImages(productImages);
		return ProductResponse.from(productId);

	}

	private void validateUser(User currentUser, ProductSaveRequest productSaveRequest) {
		if (!currentUser.getUserId().equals(productSaveRequest.getUserId())) {
			throw new UnauthorizedEnrollException(currentUser.getUserId());
		}
	}

	private void validateSaveRequest(ProductSaveRequest productSaveRequest, List<MultipartFile> images) {
		Preconditions.checkArgument(!productSaveRequest.getUserId().isBlank(),
			"등록자 아이디는 필수 값입니다.");
		Preconditions.checkArgument(!productSaveRequest.getProductName().isBlank(),
			"상품명은 필수 값입니다.");
		Preconditions.checkArgument(!productSaveRequest.getDescription().isBlank(),
			"상품 설명은 필수 값입니다.");
		Preconditions.checkArgument(!productSaveRequest.getStartDate().isBefore(LocalDateTime.now()),
			"경매 시작일이 현재 시각보다 이전일 수 없습니다.");
		Preconditions.checkArgument(!productSaveRequest.getCloseDate().isBefore(productSaveRequest.getStartDate()),
			"경매 종료일이 경매 시작일보다 이전일 수 없습니다.");
		Preconditions.checkArgument(productSaveRequest.getStartPrice() >= 1000,
			"시작 가격은 최소 1000원입니다.");
		Preconditions.checkArgument(images != null && !images.isEmpty(),
			"경매 상품에 대한 이미지는 필수 값입니다.");
	}

	private void validUpdateRequest(ProductUpdateRequest productUpdateRequest) {
		Preconditions.checkArgument(!productUpdateRequest.getProductName().isEmpty(),
			"상품명은 필수 값입니다.");
		Preconditions.checkArgument(!productUpdateRequest.getDescription().isEmpty(),
			"상품 설명은 필수 값입니다.");
		Preconditions.checkArgument(!productUpdateRequest.getStartDate().isBefore(LocalDateTime.now()),
			"경매 시작일이 현재 시각보다 이전일 수 없습니다.");
		Preconditions.checkArgument(!productUpdateRequest.getCloseDate().isBefore(productUpdateRequest.getStartDate()),
			"경매 종료일이 경매 시작일보다 이전일 수 없습니다.");
		Preconditions.checkArgument(productUpdateRequest.getStartPrice() >= 1000,
			"시작 가격은 최소 1000원입니다.");
	}
}
