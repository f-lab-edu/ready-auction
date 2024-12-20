package com.example.modulerecommendation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductImage;
import com.example.moduledomain.repository.product.ProductImageRepository;
import com.example.moduledomain.repository.product.ProductRepository;
import com.example.modulerecommendation.controller.response.ImageResponse;
import com.example.modulerecommendation.controller.response.ProductFindResponse;

@Service
public class ProductListingService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public ProductListingService(ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductFindResponse> findRecommendationProducts(List<Long> productIds) {
        List<Product> products = findByIdIn(productIds);
        List<ProductFindResponse> productFindResponses = products.stream()
            .map(this::convertToProductFindResponse)
            .collect(Collectors.toList());
        return productFindResponses;

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
        return ProductFindResponse.from(product, imageResponses);
    }
}
