package com.example.readyauction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.readyauction.domain.product.ProductLike;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
}
