package com.example.moduleapi.controller.request.product;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {
    private String productName;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime closeDate;
    private Long startPrice;

    @Builder
    public ProductUpdateRequest(String productName,
                                String description,
                                LocalDateTime startDate,
                                LocalDateTime closeDate,
                                Long startPrice) {
        this.productName = productName;
        this.description = description;
        this.startDate = startDate;
        this.closeDate = closeDate;
        this.startPrice = startPrice;
    }
}
