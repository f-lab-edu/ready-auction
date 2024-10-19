package com.example.readyauction.domain.product;

import java.time.LocalDateTime;

import com.example.readyauction.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "products")
@Getter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "productName", nullable = false)
    private String productName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "startDate", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "closeDate", nullable = false)
    private LocalDateTime closeDate;

    @Column(name = "startPrice", nullable = false)
    private int startPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductCondition productCondition;

    @Builder
    public Product(String userId, String productName, String description, LocalDateTime startDate,
        LocalDateTime closeDate, int startPrice, ProductCondition productCondition) {
        this.userId = userId;
        this.productName = productName;
        this.description = description;
        this.startDate = startDate;
        this.closeDate = closeDate;
        this.startPrice = startPrice;
        this.productCondition = productCondition;
    }

    public void updateProductInfo(String productName, String description, LocalDateTime startDate,
        LocalDateTime closeDate, int startPrice) {
        this.productName = productName;
        this.description = description;
        this.startDate = startDate;
        this.closeDate = closeDate;
        this.startPrice = startPrice;
    }

    public void updateProductCondition(ProductCondition productCondition) {
        this.productCondition = productCondition;
    }

}
