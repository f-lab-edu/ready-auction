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

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "age")
    private int age;

    @Column(name = "bid_price")
    private int price;

    @Column(name = "is_auction_successful")
    private boolean isAuctionSuccessful;

    @Builder
    public BidLogging(Gender gender, int age, int price, boolean isAuctionSuccessful) {
        this.gender = gender;
        this.age = age;
        this.price = price;
        this.isAuctionSuccessful = isAuctionSuccessful;
    }
}
