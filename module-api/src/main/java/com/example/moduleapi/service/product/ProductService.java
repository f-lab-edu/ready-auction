package com.example.moduleapi.service.product;

import com.example.moduleapi.controller.request.product.ProductSaveRequest;
import com.example.moduleapi.controller.request.product.ProductUpdateRequest;
import com.example.moduleapi.exception.product.NotFoundProductException;
import com.example.moduleapi.exception.product.ProductNotPendingException;
import com.example.moduleapi.exception.product.UnauthorizedProductAccessException;
import com.example.moduledomain.common.request.ProductFilterRequest;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductCondition;
import com.example.moduledomain.domain.user.User;
import com.example.moduledomain.repository.bidLogging.BidLoggingRepository;
import com.example.moduledomain.repository.product.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final BidLoggingRepository bidLoggingRepository;

    public ProductService(ProductRepository productRepository, BidLoggingRepository bidLoggingRepository) {
        this.productRepository = productRepository;
        this.bidLoggingRepository = bidLoggingRepository;
    }

    @Transactional
    public Product enroll(ProductSaveRequest productSaveRequest) {
        Product product = productSaveRequest.toEntity();
        Product saved = productRepository.save(product);
        return saved;
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundProductException(id));
        return product;
    }

    @Transactional(readOnly = true)
    public List<Product> findByIdIn(List<Long> productIds) {
        return productRepository.findByIdIn(productIds);
    }

    @Transactional(readOnly = true)
    public List<Product> findProductWithCriteria(ProductFilterRequest productFilterRequest) {
        List<Product> products = productRepository.findProductsWithCriteria(productFilterRequest);
        return products;
    }

    @Transactional
    public Product update(User user, Long productId, ProductUpdateRequest productUpdateRequest) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundProductException(productId));
        checkProductAccessPermission(product, user.getUserId());

        product.updateProductInfo(productUpdateRequest.getProductName(),
                                  productUpdateRequest.getDescription(),
                                  productUpdateRequest.getStartDate(),
                                  productUpdateRequest.getCloseDate(),
                                  productUpdateRequest.getStartPrice());

        return product;
    }

    @Transactional
    public Long delete(User user, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundProductException(productId));

        checkProductAccessPermission(product, user.getUserId());
        productRepository.deleteById(product.getId());

        return product.getId();
    }

    @Transactional(readOnly = true)
    public List<Product> getMyProducts(User user, Pageable pageable) {
        Page<Product> products = productRepository.findByUserId(user.getUserId(), pageable);
        return products.getContent();
    }

    @Transactional(readOnly = true)
    public List<Product> getMostBiddersProducts(User user) {
        List<Long> productIds = bidLoggingRepository.findTop10ProductIdsByGender(user.getGender());
        List<Product> products = productRepository.findByIdIn(productIds);
        Map<Long, Product> productMap = products.stream()
                                                .collect(Collectors.toMap(Product::getId, product -> product));
        List<Product> orderProducts = productIds.stream()
                                                .map(productMap::get)
                                                .filter(Objects::nonNull)
                                                .toList();
        return orderProducts;
    }

    private void checkProductAccessPermission(Product product, String userId) {
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedProductAccessException(userId, product.getId());
        }
        if (product.getProductCondition() != ProductCondition.READY) {
            throw new ProductNotPendingException(product.getId());
        }
    }

}

