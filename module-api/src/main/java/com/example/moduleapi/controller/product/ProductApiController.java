package com.example.moduleapi.controller.product;

import com.example.moduleapi.controller.request.product.ProductSaveRequest;
import com.example.moduleapi.controller.request.product.ProductUpdateRequest;
import com.example.moduleapi.controller.response.PagingResponse;
import com.example.moduleapi.controller.response.product.ProductCategoryResponse;
import com.example.moduleapi.controller.response.product.ProductLikeResponse;
import com.example.moduleapi.controller.response.product.ProductResponse;
import com.example.moduleapi.service.product.ProductFacade;
import com.example.moduledomain.common.request.ProductFilterRequest;
import com.example.moduledomain.common.response.ProductFindResponse;
import com.example.moduledomain.domain.user.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductApiController {
    private final ProductFacade productFacade;

    public ProductApiController(ProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    @PostMapping
    public ProductResponse enroll(@AuthenticationPrincipal CustomUserDetails user,
                                  @RequestPart(name = "product") ProductSaveRequest productSaveRequest,
                                  @RequestPart(name = "images") List<MultipartFile> files) {
        return productFacade.enroll(user.getUser(), productSaveRequest, files);
    }

    @GetMapping("/{id}")
    public ProductFindResponse findById(@PathVariable Long id) {
        return productFacade.findById(id);
    }

    @PostMapping("/_search")
    public PagingResponse<ProductFindResponse> findProductsByCriteriaWithRecommendations(@AuthenticationPrincipal CustomUserDetails user,
                                                                                         @RequestBody ProductFilterRequest productFilterRequest) {
        return productFacade.findProductsByCriteriaWithRecommendations(user.getUser(), productFilterRequest);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@AuthenticationPrincipal CustomUserDetails user,
                                  @PathVariable Long id,
                                  @RequestPart(name = "product") ProductUpdateRequest productUpdateRequest,
                                  @RequestPart(name = "images") List<MultipartFile> files) {
        return productFacade.update(user.getUser(), id, productUpdateRequest, files);
    }

    @DeleteMapping("/{id}")
    public ProductResponse delete(@AuthenticationPrincipal CustomUserDetails user,
                                  @PathVariable Long id) {
        return productFacade.delete(user.getUser(), id);
    }

    @GetMapping("/category")
    public List<ProductCategoryResponse> getCategory() {
        return productFacade.getCategories();
    }

    @GetMapping("/my")
    public PagingResponse<ProductFindResponse> getMyProducts(@AuthenticationPrincipal CustomUserDetails user,
                                                             @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return productFacade.getMyProducts(user.getUser(), pageable);
    }

    @GetMapping("/popular")
    public List<ProductFindResponse> getMostBiddersProducts(@AuthenticationPrincipal CustomUserDetails user) {
        return productFacade.getMostBiddersProducts(user.getUser());
    }

    @PostMapping("/{id}/likes")
    public ProductLikeResponse productLike(@AuthenticationPrincipal CustomUserDetails user,
                                           @PathVariable Long id) {
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
