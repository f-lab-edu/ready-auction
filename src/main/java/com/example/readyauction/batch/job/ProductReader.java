package com.example.readyauction.batch.job;

import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.example.readyauction.domain.product.Product;
import com.example.readyauction.repository.product.ProductRepository;

@Component
public class ProductReader implements ItemReader<List<Product>> {
    private static final int PAGE_SIZE = 500;  // 한번에 읽을 데이터 개수 (청크 사이즈)
    private final ProductRepository productRepository;
    private int currentPage = 0;

    public ProductReader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> read() throws
        Exception,
        UnexpectedInputException,
        ParseException,
        NonTransientResourceException {
        Pageable pageable = PageRequest.of(currentPage, PAGE_SIZE);
        List<Product> products = productRepository.findAll(pageable).getContent();

        if (products.isEmpty()) {
            currentPage = 0;
            return null;
        }
        currentPage++;
        return products;
    }
}
