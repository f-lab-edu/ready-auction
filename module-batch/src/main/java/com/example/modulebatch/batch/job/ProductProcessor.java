package com.example.modulebatch.batch.job;

import java.time.LocalDateTime;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductCondition;

@Component("ProductProcessor")
public class ProductProcessor implements ItemProcessor<Product, Product> {

    @Override
    public Product process(Product product) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ProductCondition productCondition = ProductCondition.from(now, product);
        product.updateProductCondition(productCondition);
        return product;
    }
}
