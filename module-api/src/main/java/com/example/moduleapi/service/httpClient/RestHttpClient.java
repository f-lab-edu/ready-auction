package com.example.moduleapi.service.httpClient;

import com.example.moduledomain.common.request.ProductFilter;
import com.example.moduledomain.common.response.ProductFindResponse;
import com.example.moduledomain.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class RestHttpClient {
    private final ProductRecommendationServerClient productRecommendationClient;

    public RestHttpClient(ProductRecommendationServerClient productRecommendationClient) {
        this.productRecommendationClient = productRecommendationClient;
    }

    // 추천 상품 을 조회하는 메서드
    public List<ProductFindResponse> findRecommendationProducts(User user, ProductFilter productFilter) {
        String authorizationHeader = generateAuthorizationHeader(user);

        return productRecommendationClient.getRecommendationProduct(
                authorizationHeader,
                user.getGender(),
                user.getAge(),
                productFilter
        );
    }

    private String generateAuthorizationHeader(User user) {
        String payload = user.getUserId() + ":" + user.getEncodedPassword();
        String token = Base64.getEncoder().encodeToString(payload.getBytes());
        return "Bearer " + token;
    }

}
