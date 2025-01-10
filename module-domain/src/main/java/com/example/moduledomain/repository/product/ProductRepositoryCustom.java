package com.example.moduledomain.repository.product;

import java.util.List;

import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.request.ProductFilterRequest;

public interface ProductRepositoryCustom {

    List<Product> findProductsWithCriteria(ProductFilterRequest productFilterRequest);
}
