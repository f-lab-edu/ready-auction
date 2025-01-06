package com.example.moduleapi.service.httpClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.moduleapi.controller.response.product.ProductFindResponse;
import com.example.moduleapi.service.httpClient.circuitBreaker.RecommendationFallBackMethod;
import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.product.ProductCondition;
import com.example.moduledomain.domain.user.Gender;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "productRecommendationClient", url = "http://127.0.0.1:8085/api/v1/products", fallback = RecommendationFallBackMethod.class)
@CircuitBreaker(name = "recommendationServer")
public interface ProductRecommendationServerClient {

    @GetMapping("/recommendations")
    List<ProductFindResponse> getRecommendationProduct(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestParam("gender") Gender gender,
        @RequestParam("age") int age,
        @RequestParam String keyword,
        @RequestParam List<Category> categories,
        @RequestParam List<ProductCondition> productConditions
    );
}
