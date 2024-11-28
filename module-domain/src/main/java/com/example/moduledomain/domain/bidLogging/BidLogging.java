package com.example.moduledomain.domain.bidLogging;

import com.example.moduledomain.domain.BaseEntity;
import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.user.Gender;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "bid_logging")
public class BidLogging extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "bid_price", nullable = false)
    private int price;

    @Column(name = "is_auction_successful", nullable = false)
    private boolean isAuctionSuccessful;

    @Builder
    public BidLogging(Long userId, Long productId, Category category, Gender gender, int age, int price, boolean isAuctionSuccessful) {
        this.userId = userId;
        this.productId = productId;
        this.category = category;
        this.gender = gender;
        this.age = age;
        this.price = price;
        this.isAuctionSuccessful = isAuctionSuccessful;
    }
}
