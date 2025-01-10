package com.example.moduleapi.service.httpClient.circuitBreaker;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.moduleapi.service.httpClient.ProductRecommendationServerClient;
import com.example.moduledomain.domain.user.Gender;
import com.example.moduledomain.request.ProductFilter;
import com.example.moduledomain.response.ProductFindResponse;

@Component
public class RecommendationFallBackMethod implements ProductRecommendationServerClient {
    /*
     * Fallback의 원리는 요청이 실패하면 Override된 메서드 실행
     * */
    @Override
    public List<ProductFindResponse> getRecommendationProduct(String authorizationHeader,
                                                              Gender gender,
                                                              int age,
                                                              ProductFilter productFilter) {
        return new ArrayList<>();
    }
}
