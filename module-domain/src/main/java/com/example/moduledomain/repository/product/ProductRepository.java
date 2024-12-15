package com.example.moduledomain.repository.product;

import com.example.moduledomain.domain.product.Category;
import com.example.moduledomain.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    Page<Product> findAllByOrderByIdDesc(Pageable pageable);

    List<Product> findByIdGreaterThanOrderByIdAsc(Long id, Pageable pageable);

    List<Product> findByCategory(Category category);

    List<Product> findByIdIn(List<Long> productIds);

}
