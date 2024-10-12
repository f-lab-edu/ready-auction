package com.example.readyauction.batch.job;

import java.time.LocalDateTime;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductCondition;

@Component
public class ProductConditionUpdate implements ItemProcessor<Product, Product> {
    // 상품의 상태를 갱신하는 로직
    @Override
    public Product process(Product product) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        // 경매 상태 업데이트
        if (now.isBefore(product.getStartDate())) {
            product.updateProductCondition(ProductCondition.READY); // 경매 대기중
        } else if (now.isAfter(product.getCloseDate())) {
            product.updateProductCondition(ProductCondition.DONE);  // 경매 종료됨
        } else {
            product.updateProductCondition(ProductCondition.ACTIVE);  // 경매 진행중
        }
        return product;
    }
}
