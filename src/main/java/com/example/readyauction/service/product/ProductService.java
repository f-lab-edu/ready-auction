package com.example.readyauction.service.product;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.readyauction.controller.request.product.ProductSaveRequest;
import com.example.readyauction.controller.request.product.ProductUpdateRequest;
import com.example.readyauction.domain.product.OrderBy;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.Status;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.exception.product.NotFoundProductException;
import com.example.readyauction.exception.product.ProductNotPendingException;
import com.example.readyauction.exception.product.UnauthorizedProductAccessException;
import com.example.readyauction.repository.ProductRepository;

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

    @Transactional
    public Product findById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundProductException(id));
        return product;
    }

    @Transactional
    public Page<Product> findProductWithCriteria(String keyword, int pageNo, int pageSize, OrderBy order) {
        Page<Product> products = productRepository.findProductsWithCriteria(keyword, pageNo, pageSize, order);
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
        if (product.getStatus() != Status.PENDING) {
            throw new ProductNotPendingException(product.getId());
        }
    }

}

