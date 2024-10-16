package com.example.readyauction.batch.job;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductCondition;
import com.example.readyauction.repository.product.ProductRepository;

@Component
public class ProductConditionWriter implements ItemWriter<Product> {

    private final ProductRepository productRepository;

    public ProductConditionWriter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void write(Chunk<? extends Product> chunk) throws Exception {
        List<? extends Product> products = chunk.getItems();
        LocalDateTime now = LocalDateTime.now();
        // 경매 상태 업데이트
        for (Product product : products) {
            if (now.isBefore(product.getStartDate())) {
                product.updateProductCondition(ProductCondition.READY); // 경매 대기중
            } else if (now.isAfter(product.getCloseDate())) {
                product.updateProductCondition(ProductCondition.DONE);  // 경매 종료됨
            } else {
                product.updateProductCondition(ProductCondition.ACTIVE);  // 경매 진행중
            }
        }
        productRepository.saveAll(products);
    }
}
