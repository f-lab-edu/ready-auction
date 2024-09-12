package com.example.readyauction.repository;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.readyauction.domain.product.OrderBy;
import com.example.readyauction.domain.product.Product;

public interface ProductRepositoryCustom {

    List<Product> findProductsWithCriteria(String keyword, int pageNo, int pageSize, OrderBy order);
}
