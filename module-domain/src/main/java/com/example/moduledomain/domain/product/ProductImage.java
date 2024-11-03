package com.example.moduledomain.domain.product;

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
@Table(name = "product_image")
@Getter
public class ProductImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    private Long productId;

    @Column(name = "originalName", nullable = false)
    private String originalName;

    @Column(name = "savedName", nullable = false)
    private String savedName;

    @Column(name = "imageFullPath", nullable = false)
    private String imageFullPath;

    @Builder
    public ProductImage(Long productId, String originalName, String savedName, String imageFullPath) {
        this.productId = productId;
        this.originalName = originalName;
        this.savedName = savedName;
        this.imageFullPath = imageFullPath;
    }
}
