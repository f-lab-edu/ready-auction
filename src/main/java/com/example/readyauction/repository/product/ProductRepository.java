package com.example.readyauction.repository.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.readyauction.domain.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    Page<Product> findAllByOrderByIdDesc(Pageable pageable);

    List<Product> findByIdGreaterThanOrderByIdAsc(Long id, Pageable pageable);

}
