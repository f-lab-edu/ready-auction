package com.example.moduleapi.service.HttpClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.moduleapi.controller.response.product.ProductFindResponse;
import com.example.moduledomain.domain.user.Gender;

@FeignClient(name = "productRecommendationClient", url = "http://127.0.0.1:8085/api/v1/products")
public interface ProductRecommendationServerClient {

    @GetMapping("/recommendations")
    List<ProductFindResponse> getRecommendationProduct(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestParam("gender") Gender gender,
        @RequestParam("age") int age,
        @RequestParam("pageNo") int pageNo,
        @RequestParam("pageSize") int pageSize
    );
}
