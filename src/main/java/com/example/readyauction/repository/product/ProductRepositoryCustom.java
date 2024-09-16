package com.example.readyauction.repository.product;

import java.util.List;

import com.example.readyauction.domain.product.OrderBy;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductCondition;

public interface ProductRepositoryCustom {

    List<Product> findProductsWithCriteria(String keyword, ProductCondition productCondition, int pageNo, int pageSize,
        OrderBy order);
}
