package com.example.readyauction.batch.job;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.readyauction.domain.product.Product;
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
        productRepository.saveAll(products);
    }
}
