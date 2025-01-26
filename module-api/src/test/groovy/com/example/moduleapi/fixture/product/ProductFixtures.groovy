package com.example.moduleapi.fixture.product

import com.example.moduledomain.common.response.ImageResponse
import com.example.moduledomain.common.response.ProductFindResponse
import com.example.moduledomain.domain.product.Category
import com.example.moduledomain.domain.product.Product
import com.example.moduledomain.domain.product.ProductCondition
import com.example.moduledomain.domain.product.ProductImage
import org.springframework.mock.web.MockMultipartFile

import java.time.LocalDateTime

class ProductFixtures {
    public static LocalDateTime 시작일 = LocalDateTime.now().plusDays(10);
    public static LocalDateTime 종료일 = 시작일.plusDays(30);
    public static LocalDateTime 과거일 = LocalDateTime.now().minusDays(1)
    public static LocalDateTime 미래일 = 시작일.plusYears(1)

    public static Category 카테고리 = Category.ELECTRONICS;

    public static READY = ProductCondition.READY
    public static ACTIVE = ProductCondition.ACTIVE
    public static DONE = ProductCondition.DONE

    static Product createProduct(Map map = [:]) {
        return Product.builder()
                .userId(map.getOrDefault("userId", "test") as String)
                .productName(map.getOrDefault("productName", "test") as String)
                .description(map.getOrDefault("description", "test") as String)
                .category(map.getOrDefault("category", Category.ELECTRONICS) as Category)
                .startDate(map.getOrDefault("startDate", 시작일) as LocalDateTime)
                .closeDate(map.getOrDefault("closeDate", 종료일) as LocalDateTime)
                .startPrice(map.getOrDefault("startPrice", 1000) as int)
                .productCondition(map.getOrDefault("productCondition", READY) as ProductCondition)
                .build()
    }

    static ProductFindResponse createProductFindResponse(Map map = [:]) {
        return ProductFindResponse.builder()
                .userId(map.getOrDefault("userId", "test") as String)
                .productName(map.getOrDefault("productName", "test") as String)
                .description(map.getOrDefault("description", "test") as String)
                .category(map.getOrDefault("category", Category.ELECTRONICS) as Category)
                .startDate(map.getOrDefault("startDate", 시작일) as LocalDateTime)
                .closeDate(map.getOrDefault("closeDate", 종료일) as LocalDateTime)
                .startPrice(map.getOrDefault("startPrice", 1000) as int)
                .recommended(map.getOrDefault("recommended", false) as boolean)
                .imageResponses([ProductFixtures.createImageResponse()])
                .build()
    }

    static MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "productImage",
                "originalProductImageFileName",
                "image/jpg",
                "productImageFileName".getBytes()
        )
    }

    static ProductImage createProductImage(Map map = [:]) {
        return ProductImage.builder()
                .productId(map.getOrDefault("productId", 1L) as Long)
                .originalName(map.getOrDefault("originalName", "originalName") as String)
                .savedName(map.getOrDefault("savedName", "savedName") as String)
                .imageFullPath(map.getOrDefault("imageFullPath", "fileTest/base/path") as String)
                .build()

    }

    static ImageResponse createImageResponse(Map map = [:]) {
        return ImageResponse.builder()
                .originalName(map.getOrDefault("originalName", "originalName") as String)
                .imagePath(map.getOrDefault("imagePath", "fileTest/base/path") as String)
                .build()
    }
}
