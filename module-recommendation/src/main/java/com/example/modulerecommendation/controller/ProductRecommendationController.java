package com.example.modulerecommendation.controller;

import com.example.moduledomain.common.request.ProductFilter;
import com.example.moduledomain.common.response.ProductFindResponse;
import com.example.moduledomain.domain.user.Gender;
import com.example.modulerecommendation.service.ProductRecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductRecommendationController {
    private final ProductRecommendationService productRecommendationService;

    public ProductRecommendationController(ProductRecommendationService productRecommendationService) {
        this.productRecommendationService = productRecommendationService;
    }

    @PostMapping("/_recommendations")
    public List<ProductFindResponse> findRecommendationProducts(@RequestParam Gender gender,
                                                                @RequestParam int age,
                                                                @RequestBody ProductFilter productFilter) {
        return productRecommendationService.getRecommendationProducts(gender, age, productFilter);
    }
}
