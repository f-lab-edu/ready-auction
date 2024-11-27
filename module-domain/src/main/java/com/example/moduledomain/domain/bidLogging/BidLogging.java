package com.example.moduledomain.domain.bidLogging;

import com.example.moduledomain.domain.BaseEntity;
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

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product_id")
    private Long productId;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "age")
    private int age;

    @Column(name = "bid_price")
    private int price;

    @Column(name = "is_auction_successful")
    private boolean isAuctionSuccessful;

    @Builder
    public BidLogging(Long userId, Long productId, Gender gender, int age, int price, boolean isAuctionSuccessful) {
        this.userId = userId;
        this.productId = productId;
        this.gender = gender;
        this.age = age;
        this.price = price;
        this.isAuctionSuccessful = isAuctionSuccessful;
    }
}
