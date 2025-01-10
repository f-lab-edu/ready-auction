package com.example.modulerecommendation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.moduledomain.domain.user.Gender;
import com.example.moduledomain.request.ProductFilter;
import com.example.moduledomain.response.ProductFindResponse;
import com.example.modulerecommendation.service.ProductRecommendationService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductRecommendationController {
    private final ProductRecommendationService productRecommendationService;

    public ProductRecommendationController(ProductRecommendationService productRecommendationService) {
        this.productRecommendationService = productRecommendationService;
    }

    @PostMapping("/_recommendations")
    public List<ProductFindResponse> findRecommendationProducts(
        @RequestParam Gender gender,
        @RequestParam int age,
        @RequestBody ProductFilter productFilter) {
        return productRecommendationService.getRecommendationProducts(gender, age, productFilter);
    }
}
