package com.example.moduleapi.service.httpClient.circuitBreaker;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.moduleapi.controller.response.product.ProductFindResponse;
import com.example.moduleapi.service.httpClient.ProductRecommendationServerClient;
import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.product.ProductCondition;
import com.example.moduledomain.domain.user.Gender;

@Component
public class RecommendationFallBackMethod implements ProductRecommendationServerClient {
    /*
     * Fallback의 원리는 요청이 실패하면 Override된 메서드 실행
     * */
    @Override
    public List<ProductFindResponse> getRecommendationProduct(String authorizationHeader,
                                                              Gender gender,
                                                              int age,
                                                              String keyword,
                                                              List<Category> categories,
                                                              List<ProductCondition> productConditions) {
        return new ArrayList<>();
    }
}
