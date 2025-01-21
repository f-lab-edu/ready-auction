package com.example.modulerecommendation.service;

import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductImage;
import com.example.moduledomain.repository.product.ProductImageRepository;
import com.example.moduledomain.repository.product.ProductRepository;
import com.example.moduledomain.response.ImageResponse;
import com.example.moduledomain.response.ProductFindResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductListingService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public ProductListingService(ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductFindResponse> findRecommendationProducts(List<Long> productIds, String keyword) {
        List<Product> products = findByIdIn(productIds);

        // keyword 필터링
        if (keyword != null && !keyword.isEmpty()) {
            products = products.stream()
                               .filter(product -> product.getProductName().contains(keyword) || product.getDescription().contains(keyword))
                               .collect(Collectors.toList());
        }

        List<ProductFindResponse> productFindResponses = products.stream()
                                                                 .map(this::convertToProductFindResponse)
                                                                 .collect(Collectors.toList());

        Collections.shuffle(productFindResponses);
        return productFindResponses.subList(0, Math.min(productFindResponses.size(), 3));
    }

    @Transactional(readOnly = true)
    public List<Product> findByIdIn(List<Long> productIds) {
        return productRepository.findByIdIn(productIds);
    }

    @Transactional(readOnly = true)
    public List<ProductImage> getImage(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);
        return productImages;
    }

    private ProductFindResponse convertToProductFindResponse(Product product) {
        List<ProductImage> productImages = getImage(product.getId());
        List<ImageResponse> imageResponses = productImages.stream()
                                                          .map(ImageResponse::from)
                                                          .collect(Collectors.toList());
        return ProductFindResponse.from(product, imageResponses, true);
    }
}
