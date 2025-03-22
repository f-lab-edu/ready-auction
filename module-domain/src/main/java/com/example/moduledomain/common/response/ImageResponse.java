package com.example.moduledomain.common.response;

import com.example.moduledomain.domain.product.ProductImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImageResponse {
    private Long id;
    private String originalName;
    private String imagePath;

    @Builder
    public ImageResponse(Long id, String originalName, String imagePath) {
        this.id = id;
        this.originalName = originalName;
        this.imagePath = imagePath;
    }


    public static ImageResponse from(ProductImage productImage) {
        return new ImageResponse(productImage.getId(), productImage.getOriginalName(), productImage.getImageFullPath());
    }
}
