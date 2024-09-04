package com.example.readyauction.repository;

import org.springframework.data.domain.Page;

import com.example.readyauction.domain.product.Product;
import com.example.readyauction.service.product.OrderBy;

public interface ProductRepositoryCustom {

    Page<Product> findProductsWithCriteria(String keyword, int pageNo, int pageSize, OrderBy order);
}
