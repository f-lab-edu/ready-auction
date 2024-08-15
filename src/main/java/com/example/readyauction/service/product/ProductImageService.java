package com.example.readyauction.service.product;

import com.example.readyauction.domain.product.ProductImage;
import com.example.readyauction.repository.ProductImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageService {

    private final ProductImageRepository productImageRepository;

    public ProductImageService(ProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }

    public void saveImage(List<ProductImage> productImages) {
        productImageRepository.saveAll(productImages);
    }

    public List<ProductImage> getImage(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);
        return productImages;
    }

    public void updateImage(Long productId, List<ProductImage> productImages) {
        this.deleteImage(productId);
        this.saveImage(productImages);
    }

    public List<ProductImage> deleteImage(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);
        for (ProductImage productImage : productImages) {
            productImageRepository.deleteById(productImage.getId());
        }
        return productImages;

    }
}
