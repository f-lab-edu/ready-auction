package com.example.moduledomain.domain.auction;

import com.example.moduledomain.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "auctions")
@Getter
public class Auction extends BaseEntity { // 경매 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "product_Id", nullable = false, updatable = false)
    private Long productId; // 상품 ID

    @Column(name = "user_Id", nullable = false)
    private String userId; // 현재 최고가를 부른 유저 Id

    @Column(name = "best_price", nullable = false)
    private int bestPrice; // 최고가를 부른 유저가 부른 최고가

    @Builder
    public Auction(Long productId, String userId, int bestPrice) {
        this.productId = productId;
        this.userId = userId;
        this.bestPrice = bestPrice;
    }

    public void updateActionInfo(String userId, int biddingPrice) {
        this.userId = userId;
        this.bestPrice = biddingPrice;

    }
}
