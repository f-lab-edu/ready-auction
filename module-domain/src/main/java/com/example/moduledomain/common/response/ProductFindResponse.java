package com.example.moduledomain.common.response;

import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.product.Product;
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
    private Category category;
    private LocalDateTime startDate;
    private LocalDateTime closeDate;
    private int startPrice;
    private boolean recommended;

    @Builder
    public ProductFindResponse(String userId,
                               List<ImageResponse> imageResponses,
                               String productName,
                               String description,
                               Category category,
                               LocalDateTime startDate,
                               LocalDateTime closeDate,
                               int startPrice,
                               boolean recommended) {
        this.userId = userId;
        this.imageResponses = imageResponses;
        this.productName = productName;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.closeDate = closeDate;
        this.startPrice = startPrice;
        this.recommended = recommended;
    }

    public static ProductFindResponse from(Product product,
                                           List<ImageResponse> imageResponses,
                                           boolean recommended) {
        return ProductFindResponse.builder()
                                  .userId(product.getUserId())
                                  .imageResponses(imageResponses)
                                  .productName(product.getProductName())
                                  .description(product.getDescription())
                                  .category(product.getCategory())
                                  .startDate(product.getStartDate())
                                  .closeDate(product.getCloseDate())
                                  .startPrice(product.getStartPrice())
                                  .recommended(recommended)
                                  .build();

    }
}
