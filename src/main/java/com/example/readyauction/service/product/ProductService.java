package com.example.readyauction.service.product;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.readyauction.controller.request.product.ProductSaveRequest;
import com.example.readyauction.controller.request.product.ProductUpdateRequest;
import com.example.readyauction.domain.product.OrderBy;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductCondition;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.exception.product.NotFoundProductException;
import com.example.readyauction.exception.product.ProductNotPendingException;
import com.example.readyauction.exception.product.UnauthorizedProductAccessException;
import com.example.readyauction.repository.product.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product enroll(ProductSaveRequest productSaveRequest) {
        Product product = productSaveRequest.toEntity();
        Product saved = productRepository.save(product);
        return saved;
    }

    @Transactional(readOnly = true)
    public Product findById(Long id, LocalDateTime requestTime) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundProductException(id));

        if (requestTime.isAfter(product.getStartDate()) && requestTime.isBefore(product.getCloseDate())) {
            product.updateProductCondition(ProductCondition.ACTIVE);
        } else if (requestTime.isAfter(product.getCloseDate())) {
            product.updateProductCondition(ProductCondition.DONE);
        }
        return product;
    }

    @Transactional(readOnly = true)
    public List<Product> findProductWithCriteria(String keyword, ProductCondition productCondition, int pageNo,
        int pageSize,
        OrderBy order) {
        if (order == null)
            order = OrderBy.LATEST;
        List<Product> products = productRepository.findProductsWithCriteria(keyword, productCondition, pageNo, pageSize,
            order);
        return products;
    }

    @Transactional
    public Product update(User user, Long productId, ProductUpdateRequest productUpdateRequest) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundProductException(productId));
        checkProductAccessPermission(product, user.getUserId());

        product.updateProductInfo(
            productUpdateRequest.getProductName(),
            productUpdateRequest.getDescription(),
            productUpdateRequest.getStartDate(),
            productUpdateRequest.getCloseDate(),
            productUpdateRequest.getStartPrice()
        );

        return product;
    }

    @Transactional
    public Long delete(User user, Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundProductException(productId));

        checkProductAccessPermission(product, user.getUserId());
        productRepository.deleteById(product.getId());

        return product.getId();
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

