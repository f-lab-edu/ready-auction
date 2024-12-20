package com.example.modulerecommendation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
        @RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize) {
        return productRecommendationService.getRecommendationProducts(gender, age, pageNo, pageSize);
    }
}
