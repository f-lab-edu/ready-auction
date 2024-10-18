package com.example.readyauction.batch.job;

import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.example.readyauction.domain.product.Product;
import com.example.readyauction.repository.product.ProductRepository;

@Component
public class ProductReader implements ItemReader<List<Product>> {
    @Value("${ready.auction.spring.batch.chunkSize}")
    private int PAGE_SIZE;  // 한번에 읽을 데이터 개수 (청크 사이즈)

    private final ProductRepository productRepository;
    private Long lastReadProductId = 1L;

    public ProductReader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> read() throws
        Exception,
        UnexpectedInputException,
        ParseException,
        NonTransientResourceException {
        List<Product> products = productRepository.findByIdGreaterThanOrderByIdAsc(lastReadProductId,
            PageRequest.of(0, PAGE_SIZE));

        if (products.isEmpty()) {
            lastReadProductId = 1L;
            return null;
        }
        lastReadProductId = products.get(products.size() - 1).getId();
        return products;
    }
}
