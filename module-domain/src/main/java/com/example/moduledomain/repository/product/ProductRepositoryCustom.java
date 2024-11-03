package com.example.moduledomain.repository.product;

import java.util.List;

import com.example.moduledomain.domain.product.OrderBy;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductCondition;

public interface ProductRepositoryCustom {

    List<Product> findProductsWithCriteria(String keyword, ProductCondition productCondition, int pageNo, int pageSize,
        OrderBy order);
}
