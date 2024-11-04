package com.example.modulebatch.batch.job;

import com.example.modulebatch.batch.config.BatchConfig;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.repository.product.ProductRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("productReader")
public class ProductReader implements ItemReader<List<Product>> {
    private int PAGE_SIZE = BatchConfig.CHUNK_SIZE;  // 한번에 읽을 데이터 개수 (청크 사이즈)

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
