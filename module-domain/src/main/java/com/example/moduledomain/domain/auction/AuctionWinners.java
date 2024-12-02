package com.example.moduledomain.domain.auction;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "auction_winners")
@Getter
public class AuctionWinners {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_Id")
    private Long userId;

    @Column(name = "product_Id")
    private Long productId;

    @Column(name = "price")
    private int price;
    
    @Builder
    public AuctionWinners(Long id, Long userId, Long productId, int price) {
        this.userId = userId;
        this.productId = productId;
        this.price = price;
    }
}
