package com.example.moduledomain.repository.product;

import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    List<Product> findByCategory(Category category);

    List<Product> findByIdIn(List<Long> productIds);

}
