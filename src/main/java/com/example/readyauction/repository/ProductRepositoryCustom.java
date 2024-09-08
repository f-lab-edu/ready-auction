package com.example.readyauction.repository;

import org.springframework.data.domain.Page;

import com.example.readyauction.domain.product.OrderBy;
import com.example.readyauction.domain.product.Product;

public interface ProductRepositoryCustom {

    Page<Product> findProductsWithCriteria(String keyword, int pageNo, int pageSize, OrderBy order);
}
