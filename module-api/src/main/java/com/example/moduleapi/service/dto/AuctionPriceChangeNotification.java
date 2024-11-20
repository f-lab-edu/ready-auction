package com.example.moduleapi.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuctionPriceChangeNotification {
    private Long productId;
    private Long newPrice;

    @Builder
    public AuctionPriceChangeNotification(Long productId, Long newPrice) {
        this.productId = productId;
        this.newPrice = newPrice;
    }
}
