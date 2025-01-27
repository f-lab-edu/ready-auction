package com.example.moduleapi.controller.product;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.moduleapi.controller.request.product.ProductSaveRequest;
import com.example.moduleapi.controller.request.product.ProductUpdateRequest;
import com.example.moduleapi.controller.response.PagingResponse;
import com.example.moduleapi.controller.response.product.ProductLikeResponse;
import com.example.moduleapi.controller.response.product.ProductResponse;
import com.example.moduleapi.service.product.ProductFacade;
import com.example.moduledomain.domain.user.CustomUserDetails;
import com.example.moduledomain.request.ProductFilterRequest;
import com.example.moduledomain.response.ProductFindResponse;

@RestController
@RequestMapping("/api/v1/products")
public class ProductApiController {
    private final ProductFacade productFacade;
    private static final String DEFAULT_SIZE = "6";

    public ProductApiController(ProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    @PostMapping
    public ProductResponse enroll(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestPart(name = "product") ProductSaveRequest productSaveRequest,
        @RequestPart(name = "images") List<MultipartFile> files) {
        return productFacade.enroll(user.getUser(), productSaveRequest, files);
    }

    @GetMapping("/{id}")
    public ProductFindResponse findById(@PathVariable Long id) {
        return productFacade.findById(id);
    }

    @PostMapping("/_search")
    public PagingResponse<ProductFindResponse> findProductsByCriteriaWithRecommendations(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestBody ProductFilterRequest productFilterRequest) {
        return productFacade.findProductsByCriteriaWithRecommendations(user.getUser(), productFilterRequest);
    }

    @PutMapping("/{id}")
    public ProductResponse update(
        @AuthenticationPrincipal CustomUserDetails user,
        @PathVariable Long id,
        @RequestPart(name = "product") ProductUpdateRequest productUpdateRequest,
        @RequestPart(name = "images") List<MultipartFile> files) {
        return productFacade.update(user.getUser(), id, productUpdateRequest, files);
    }

    @DeleteMapping("/{id}")
    public ProductResponse delete(
        @AuthenticationPrincipal CustomUserDetails user,
        @PathVariable Long id) {
        return productFacade.delete(user.getUser(), id);
    }

    @PostMapping("/{id}/likes")
    public ProductLikeResponse productLike(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id) {
        return productFacade.addLike(user.getUser(), id);
    }

    @DeleteMapping("/{id}/likes")
    public ProductLikeResponse productLikeDelete(@AuthenticationPrincipal CustomUserDetails user,
                                                 @PathVariable Long id) {
        return productFacade.productLikeDelete(user.getUser(), id);
    }

    @GetMapping("{id}/likes")
    public ProductLikeResponse getProductLike(@PathVariable Long id) {
        return productFacade.getProductLikes(id);
    }
}
