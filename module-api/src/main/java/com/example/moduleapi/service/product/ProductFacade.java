package com.example.moduleapi.service.product;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.moduleapi.controller.request.product.ProductSaveRequest;
import com.example.moduleapi.controller.request.product.ProductUpdateRequest;
import com.example.moduleapi.controller.response.ImageResponse;
import com.example.moduleapi.controller.response.PagingResponse;
import com.example.moduleapi.controller.response.product.ProductFindResponse;
import com.example.moduleapi.controller.response.product.ProductLikeResponse;
import com.example.moduleapi.controller.response.product.ProductResponse;
import com.example.moduleapi.exception.product.UnauthorizedEnrollException;
import com.example.moduleapi.service.file.FileService;
import com.example.moduledomain.domain.product.OrderBy;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductCondition;
import com.example.moduledomain.domain.product.ProductImage;
import com.example.moduledomain.domain.user.CustomUserDetails;
import com.example.moduledomain.domain.user.User;
import com.google.common.base.Preconditions;

@Service
@Profile("dev")
public class ProductFacade {
    private final FileService fileService;
    private final ProductImageService productImageService;
    private final ProductService productService;
    private final ProductLikeService productLikeService;

    public ProductFacade(FileService fileService, ProductImageService productImageService,
        ProductService productService,
        ProductLikeService productLikeService) {
        this.fileService = fileService;
        this.productImageService = productImageService;
        this.productService = productService;
        this.productLikeService = productLikeService;
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

    @Transactional(readOnly = true)
    public ProductFindResponse findById(Long productId) {
        Product product = productService.findById(productId);
        List<ProductImage> productImages = productImageService.getImage(productId);
        List<ImageResponse> imageResponses = fileService.loadImages(productImages);
        return ProductFindResponse.from(product, imageResponses);
    }

    @Transactional(readOnly = true)
    public PagingResponse<ProductFindResponse> findAll(CustomUserDetails customUserDetails, String keyword,
        ProductCondition productCondition, int pageNo, int pageSize, OrderBy order) {
        List<Product> products = productService.findProductWithCriteria(keyword, productCondition, pageNo, pageSize,
            order);
        List<ProductFindResponse> productFindResponses = products.stream()
            .map(this::convertToProductFindResponse)
            .collect(Collectors.toList());

        if (keyword == null && productCondition == null && order == null) {
            List<ProductFindResponse> recommendationProducts = findRecommendationProducts(customUserDetails.getUser(),
                pageNo, pageSize);
            List<ProductFindResponse> result = combineAndGetProducts(productFindResponses,
                recommendationProducts);
            return PagingResponse.from(result, pageNo);
        }

        return PagingResponse.from(productFindResponses, pageNo);
    }

    @Value("${ready.auction.recommendation.server.base.url}")
    private String productRecommendationServerBaseUrl;

    private List<ProductFindResponse> findRecommendationProducts(User user, int pageNo, int pageSize) {
        String payload = user.getUserId() + ":" + user.getEncodedPassword();
        String token = Base64.getEncoder().encodeToString(payload.getBytes());
        String BaseUrl = productRecommendationServerBaseUrl + "/products/recommendations";

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(BaseUrl)
            .queryParam("gender", user.getGender())
            .queryParam("age", user.getAge())
            .queryParam("pageNo", pageNo)
            .queryParam("pageSize", pageSize);

        RestClient client = RestClient.create();
        List<ProductFindResponse> entity = client.get()
            .uri(uriBuilder.toUriString())
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .body(List.class);

        return entity;
    }

    private List<ProductFindResponse> combineAndGetProducts(List<ProductFindResponse> original,
        List<ProductFindResponse> recommendations) {
        original.addAll(recommendations);
        Collections.shuffle(original);

        return original.stream()
            .limit(6)
            .collect(Collectors.toList());
    }

    private ProductFindResponse convertToProductFindResponse(Product product) {
        List<ProductImage> productImages = productImageService.getImage(product.getId());
        List<ImageResponse> imageResponses = fileService.loadImages(productImages);
        return ProductFindResponse.from(product, imageResponses);
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

    @Transactional
    public ProductLikeResponse addLike(User user, Long productId) {
        return productLikeService.addLike(user, productId);
    }

    @Transactional
    public ProductLikeResponse productLikeDelete(User user, Long productId) {
        return productLikeService.deleteLike(user, productId);
    }

    @Transactional(readOnly = true)
    public ProductLikeResponse getProductLikes(Long productId) {
        return productLikeService.getProductLikesByProductId(productId);
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
        Preconditions.checkArgument(!ObjectUtils.isEmpty(productSaveRequest.getCategory()), "카테고리를 설정해주세요.");
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
