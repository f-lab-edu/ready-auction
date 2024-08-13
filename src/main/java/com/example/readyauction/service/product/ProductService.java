package com.example.readyauction.service.product;

import com.example.readyauction.controller.request.product.ProductSaveRequest;
import com.example.readyauction.controller.request.product.ProductUpdateRequest;
import com.example.readyauction.controller.response.ImageResponse;
import com.example.readyauction.controller.response.product.ProductFindResponse;
import com.example.readyauction.controller.response.product.ProductResponse;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductImage;
import com.example.readyauction.domain.product.Status;
import com.example.readyauction.exception.product.NotFoundProductException;
import com.example.readyauction.exception.product.ProductNotPendingException;
import com.example.readyauction.exception.product.UnauthorizedProductAccessException;
import com.example.readyauction.repository.ProductImageRepository;
import com.example.readyauction.repository.ProductRepository;
import com.example.readyauction.service.file.FileService;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final FileService fileService;

    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository,
                          FileService fileService) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.fileService = fileService;
    }

    @Transactional
    public ProductResponse enroll(String userId, ProductSaveRequest productSaveRequest, List<MultipartFile> files) {
        validateSaveRequest(productSaveRequest);

        Product product = productSaveRequest.toEntity(userId);
        Product saved = productRepository.save(product);

        if (!files.isEmpty()) {
            List<ProductImage> productImages = fileService.uploadImages(userId, files, product);
            productImageRepository.saveAll(productImages);
        }
        return ProductResponse.from(saved.getId());
    }

    @Transactional
    public ProductFindResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundProductException(id));
        List<ProductImage> productImages = productImageRepository.findByProductId(product.getId());
        List<ImageResponse> imageResponses = fileService.loadImages(productImages);
        return new ProductFindResponse().from(product, imageResponses);
    }

    @Transactional
    public ProductResponse update(String userId, Long productId, ProductUpdateRequest productUpdateRequest,
                                  List<MultipartFile> files) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundProductException(productId));
        checkProductAccessPermission(productId, userId);

        validUpdateRequest(productUpdateRequest);

        product.updateProductInfo(
                productUpdateRequest.getProductName(),
                productUpdateRequest.getDescription(),
                productUpdateRequest.getStartDate(),
                productUpdateRequest.getCloseDate(),
                productUpdateRequest.getStartPrice()
        );

        if (!files.isEmpty()) {
            List<ProductImage> loadProductImages = productImageRepository.findByProductId(product.getId());
            for (ProductImage productImage : loadProductImages) {
                productImageRepository.deleteById(productImage.getId());
            }
            List<ImageResponse> imageResponses = fileService.loadImages(loadProductImages);
            fileService.delete(imageResponses);
            // 재업로드
            List<ProductImage> updatedProductImages = fileService.uploadImages(userId, files, product);
            productImageRepository.saveAll(updatedProductImages);

        }
        return ProductResponse.from(product.getId());
    }

    @Transactional
    public ProductResponse delete(String userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundProductException(productId));
        checkProductAccessPermission(productId, userId);
        List<ProductImage> productImages = productImageRepository.findByProductId(product.getId());
        List<ImageResponse> imageResponses = fileService.loadImages(productImages);

        fileService.delete(imageResponses);
        productRepository.deleteById(productId);

        return ProductResponse.from(product.getId());
    }

    private void checkProductAccessPermission(Long id, String userId) {
        Product product = productRepository.findById(id).get();
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedProductAccessException(userId, id);
        }
        if (product.getStatus() != Status.PENDING) {
            throw new ProductNotPendingException(id);
        }
    }

    private void validateSaveRequest(ProductSaveRequest productSaveRequest) {
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
