package com.example.readyauction.service.product;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.readyauction.controller.request.product.ProductSaveRequest;
import com.example.readyauction.controller.request.product.ProductUpdateRequest;
import com.example.readyauction.controller.response.ImageResponse;
import com.example.readyauction.controller.response.product.ProductFindResponse;
import com.example.readyauction.controller.response.product.ProductResponse;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductImage;
import com.example.readyauction.domain.product.Status;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.exception.product.NotFoundProductException;
import com.example.readyauction.exception.product.ProductNotPendingException;
import com.example.readyauction.repository.ProductRepository;
import com.example.readyauction.service.file.FileService;
import com.google.common.base.Preconditions;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final FileService fileService;

	public ProductService(ProductRepository productRepository, FileService fileService) {
		this.productRepository = productRepository;
		this.fileService = fileService;
	}

	@Transactional
	public ProductResponse enroll(User user, ProductSaveRequest productSaveRequest, List<MultipartFile> files) {
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

		Product product = productSaveRequest.toEntity();
		product.mappingUser(user);
		// 이미지 처리
		if (!files.isEmpty()) {
			List<ProductImage> productImages = fileService.uploadImages(user.getUserId(), files);
			productImages.forEach(productImage -> productImage.mappingProduct(product));
		}
		Product saved = productRepository.save(product);
		return ProductResponse.from(saved.getId());
	}

	@Transactional
	public ProductFindResponse findById(Long id) {
		Product product = productRepository.findById(id)
			.orElseThrow(() -> new NotFoundProductException(id));
		List<ImageResponse> imageResponses = fileService.loadImages(product);
		return new ProductFindResponse().from(product, imageResponses);
	}

	@Transactional
	public ProductResponse update(User user, Long productId, ProductUpdateRequest productUpdateRequest,
		List<MultipartFile> files) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new NotFoundProductException(productId));
		checkProductAccessPermission(productId);

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

		product.updateProductInfo(productUpdateRequest); // 필드들을 따로 넘기기에는 넘 길어서 그냥 DTO를 넘김.
		// 우선 다 삭제하고 다시 업로드하는걸로
		if (!files.isEmpty()) {
			List<ImageResponse> imageResponses = fileService.loadImages(product);
			fileService.deleteImagesDB(product); // DB에서도 삭제
			fileService.deleteImagesLocal(imageResponses); // 로컬에서 삭제
			// 재업로드
			List<ProductImage> productImages = fileService.uploadImages(user.getUserId(), files);
			productImages.forEach(productImage -> productImage.mappingProduct(product));

		}
		return ProductResponse.from(product.getId());
	}

	@Transactional
	public ProductResponse delete(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new NotFoundProductException(productId));
		checkProductAccessPermission(productId);
		List<ImageResponse> imageResponses = fileService.loadImages(product);
		fileService.deleteImagesLocal(imageResponses); // 로컬에서 삭제
		productRepository.deleteById(productId); // 상품 삭제 -> 이미지는 cascade = REMOVE 걸려있음.
		return ProductResponse.from(product.getId());
	}

	private void checkProductAccessPermission(Long id) {
		Product product = productRepository.findById(id).get();
		if (product.getStatus() != Status.PENDING) {
			throw new ProductNotPendingException(id);
		}
	}
}
