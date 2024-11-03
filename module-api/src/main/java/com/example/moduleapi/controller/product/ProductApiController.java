package com.example.moduleapi.controller.product;

import com.example.moduleapi.controller.request.product.ProductSaveRequest;
import com.example.moduleapi.controller.request.product.ProductUpdateRequest;
import com.example.moduleapi.controller.response.PagingResponse;
import com.example.moduleapi.controller.response.product.ProductFindResponse;
import com.example.moduleapi.controller.response.product.ProductLikeResponse;
import com.example.moduleapi.controller.response.product.ProductResponse;
import com.example.moduleapi.service.product.ProductFacade;
import com.example.moduledomain.domain.product.OrderBy;
import com.example.moduledomain.domain.product.ProductCondition;
import com.example.moduledomain.domain.user.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductApiController {
    private final ProductFacade productFacade;
    private static final String DEFAULT_SIZE = "9";

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

    @GetMapping
    public PagingResponse<ProductFindResponse> findAll(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "productCondition", required = false) ProductCondition productCondition,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_SIZE, required = false) int pageSize,
            @RequestParam(value = "orderBy", required = false) OrderBy order) {
        return productFacade.findAll(keyword, productCondition, pageNo, pageSize, order);
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
