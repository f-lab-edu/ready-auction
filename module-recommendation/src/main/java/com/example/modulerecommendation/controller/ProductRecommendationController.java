package com.example.modulerecommendation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.product.ProductCondition;
import com.example.moduledomain.domain.user.Gender;
import com.example.modulerecommendation.controller.response.ProductFindResponse;
import com.example.modulerecommendation.service.ProductRecommendationService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductRecommendationController {
    private final ProductRecommendationService productRecommendationService;

    public ProductRecommendationController(ProductRecommendationService productRecommendationService) {
        this.productRecommendationService = productRecommendationService;
    }

    @GetMapping("/recommendations")
    public List<ProductFindResponse> findRecommendationProducts(
        @RequestParam Gender gender,
        @RequestParam int age,
        @RequestParam String keyword,
        @RequestParam List<Category> categories,
        @RequestParam List<ProductCondition> productConditions) {
        return productRecommendationService.getRecommendationProducts(gender, age, keyword, categories,
            productConditions);
    }
}
