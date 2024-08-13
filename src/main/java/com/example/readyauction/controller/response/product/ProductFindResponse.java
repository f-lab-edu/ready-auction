package com.example.readyauction.controller.response.product;

import com.example.readyauction.controller.response.ImageResponse;
import com.example.readyauction.domain.product.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProductFindResponse {
    private String userId;
    private List<ImageResponse> imageResponses;
    private String productName;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime closeDate;
    private int startPrice;

    @Builder
    public ProductFindResponse(String userId, List<ImageResponse> imageResponses, String productName,
                               String description, LocalDateTime startDate, LocalDateTime closeDate, int startPrice) {
        this.userId = userId;
        this.imageResponses = imageResponses;
        this.productName = productName;
        this.description = description;
        this.startDate = startDate;
        this.closeDate = closeDate;
        this.startPrice = startPrice;
    }

    public ProductFindResponse from(Product product, List<ImageResponse> imageResponses) {
        return ProductFindResponse.builder()
                .userId(product.getUserId())
                .imageResponses(imageResponses)
                .productName(product.getProductName())
                .description(product.getDescription())
                .startDate(product.getStartDate())
                .closeDate(product.getCloseDate())
                .startPrice(product.getStartPrice())
                .build();

    }
}
