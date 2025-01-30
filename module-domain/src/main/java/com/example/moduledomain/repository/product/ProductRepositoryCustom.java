package com.example.moduledomain.repository.product;

import com.example.moduledomain.common.request.ProductFilterRequest;
import com.example.moduledomain.domain.product.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> findProductsWithCriteria(ProductFilterRequest productFilterRequest);
}
