package com.example.readyauction.controller.product;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.readyauction.controller.request.product.ProductSaveRequest;
import com.example.readyauction.controller.request.product.ProductUpdateRequest;
import com.example.readyauction.controller.response.product.ProductFindResponse;
import com.example.readyauction.controller.response.product.ProductPagingResponse;
import com.example.readyauction.controller.response.product.ProductResponse;
import com.example.readyauction.domain.user.CustomUserDetails;
import com.example.readyauction.service.product.OrderBy;
import com.example.readyauction.service.product.ProductFacade;

@RestController
@RequestMapping("/api/v1/products")
public class ProductApiController {
    private static final String DEFAULT_SIZE = "9";
    private final ProductFacade productFacade;

    public ProductApiController(ProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    @PostMapping
    public ProductResponse enroll(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestPart(name = "product") ProductSaveRequest productSaveRequest,
        @RequestPart(name = "Images") List<MultipartFile> files) {
        ProductResponse productSaveResponse = productFacade.enroll(user.getUser(), productSaveRequest, files);
        return productSaveResponse;
    }

    @GetMapping("/{id}")
    public ProductFindResponse findById(@PathVariable Long id) {
        ProductFindResponse productFindResponse = productFacade.findById(id);
        return productFindResponse;
    }

    @GetMapping
    
    public ProductPagingResponse findAll(
        @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
        @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
        @RequestParam(value = "pageSize", defaultValue = DEFAULT_SIZE, required = false) int pageSize,
        @RequestParam(value = "orderBy", required = false) OrderBy order) {
        ProductPagingResponse productPagingResponse = productFacade.findAll(keyword, pageNo, pageSize, order);
        return productPagingResponse;
    }

    @PutMapping("/{id}")
    public ProductResponse update(
        @AuthenticationPrincipal CustomUserDetails user,
        @PathVariable Long id,
        @RequestPart(name = "product") ProductUpdateRequest productUpdateRequest,
        @RequestPart(name = "Images") List<MultipartFile> files) {
        ProductResponse ProductUpdateResponse = productFacade.update(user.getUser(), id, productUpdateRequest, files);
        return ProductUpdateResponse;
    }

    @DeleteMapping("/{id}")
    public ProductResponse delete(
        @AuthenticationPrincipal CustomUserDetails user,
        @PathVariable Long id) {
        ProductResponse ProductDeleteResponse = productFacade.delete(user.getUser(), id);
        return ProductDeleteResponse;
    }
}
