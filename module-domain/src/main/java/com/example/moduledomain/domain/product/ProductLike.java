package com.example.moduledomain.domain.product;

import com.example.moduledomain.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "productLikes")
@Getter
public class ProductLike extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "product_id", nullable = false, updatable = false)
    private Long productId;

    @Column(name = "like_count")
    private int likeCount;

    public ProductLike(Long productId, int likeCount) {
        this.productId = productId;
        this.likeCount = likeCount;
    }
}
