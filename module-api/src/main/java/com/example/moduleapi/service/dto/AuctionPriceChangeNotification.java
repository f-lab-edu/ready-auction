package com.example.moduleapi.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuctionPriceChangeNotification {
    private Long productId;
    private int newPrice;

    @Builder
    public AuctionPriceChangeNotification(Long productId, int newPrice) {
        this.productId = productId;
        this.newPrice = newPrice;
    }
}
