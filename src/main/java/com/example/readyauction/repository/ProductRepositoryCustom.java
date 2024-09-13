package com.example.readyauction.repository;

import java.util.List;

import com.example.readyauction.domain.product.OrderBy;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.Status;

public interface ProductRepositoryCustom {

    List<Product> findProductsWithCriteria(String keyword, Status status, int pageNo, int pageSize, OrderBy order);
}
