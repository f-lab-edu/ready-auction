package com.example.moduledomain.domain.product;

import com.example.moduledomain.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    @Column(name = "category", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductCondition productCondition;

    @Builder
    public Product(String userId, String productName, String description, LocalDateTime startDate, LocalDateTime closeDate, int startPrice, Category category, ProductCondition productCondition) {
        this.userId = userId;
        this.productName = productName;
        this.description = description;
        this.startDate = startDate;
        this.closeDate = closeDate;
        this.startPrice = startPrice;
        this.category = category;
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
