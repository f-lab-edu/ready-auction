package com.example.readyauction.service.product;

import com.example.readyauction.controller.request.product.ProductSaveRequest;
import com.example.readyauction.controller.request.product.ProductUpdateRequest;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.Status;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.exception.product.NotFoundProductException;
import com.example.readyauction.exception.product.ProductNotPendingException;
import com.example.readyauction.exception.product.UnauthorizedProductAccessException;
import com.example.readyauction.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Product update(User user, Long productId, ProductUpdateRequest productUpdateRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundProductException(productId));
        checkProductAccessPermission(productId, user.getUserId());

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
    public Long delete(String userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundProductException(productId));

        checkProductAccessPermission(productId, userId);
        productRepository.deleteById(product.getId());

        return product.getId();
    }

    private void checkProductAccessPermission(Long id, String userId) {
        Product product = productRepository.findById(id).get();
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedProductAccessException(userId, id);
        }
        if (product.getStatus() != Status.PENDING) {
            throw new ProductNotPendingException(id);
        }
    }

}
