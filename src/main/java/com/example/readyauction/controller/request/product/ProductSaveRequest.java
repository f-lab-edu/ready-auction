package com.example.readyauction.controller.request.product;

import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ProductSaveRequest {
    private String userId;
    private String productName;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime closeDate;
    private int startPrice;

    public ProductSaveRequest(String userId,
                              String productName,
                              String description,
                              LocalDateTime startDate,
                              LocalDateTime closeDate,
                              int startPrice) {
        this.userId = userId;
        this.productName = productName;
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
                .startDate(startDate)
                .closeDate(closeDate)
                .startPrice(startPrice)
                .status(Status.PENDING)
                .build();
    }

}
