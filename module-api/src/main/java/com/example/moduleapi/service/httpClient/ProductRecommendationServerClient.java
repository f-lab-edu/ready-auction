package com.example.moduleapi.service.httpClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.moduleapi.service.httpClient.circuitBreaker.RecommendationFallBackMethod;
import com.example.moduledomain.domain.user.Gender;
import com.example.moduledomain.request.ProductFilter;
import com.example.moduledomain.response.ProductFindResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "productRecommendationClient", url = "http://127.0.0.1:8085/api/v1/products", fallback = RecommendationFallBackMethod.class)
@CircuitBreaker(name = "recommendationServer")
public interface ProductRecommendationServerClient {

    @PostMapping("/_recommendations")
    List<ProductFindResponse> getRecommendationProduct(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestParam("gender") Gender gender,
        @RequestParam("age") int age,
        @RequestBody ProductFilter productFilter
    );
}
