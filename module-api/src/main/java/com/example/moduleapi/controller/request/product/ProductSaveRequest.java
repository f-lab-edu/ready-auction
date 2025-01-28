package com.example.moduleapi.controller.request.product;

import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductCondition;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ProductSaveRequest {
    private String userId;
    private String productName;
    private Category category;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime closeDate;
    private Long startPrice;

    @Builder
    public ProductSaveRequest(String userId,
                              String productName,
                              Category category,
                              String description,
                              LocalDateTime startDate,
                              LocalDateTime closeDate,
                              Long startPrice) {
        this.userId = userId;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.startDate = startDate;
        this.closeDate = closeDate;
        this.startPrice = startPrice;
    }

    public Product toEntity() {
        return Product.builder()
                      .userId(userId)
                      .productName(productName)
                      .description(description)
                      .category(category)
                      .startDate(startDate)
                      .closeDate(closeDate)
                      .startPrice(startPrice)
                      .productCondition(ProductCondition.READY)
                      .build();
    }

}
