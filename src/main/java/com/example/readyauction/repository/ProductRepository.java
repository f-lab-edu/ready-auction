package com.example.readyauction.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.readyauction.domain.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findAllByOrderByIdDesc(Pageable pageable);

	List<Product> findAllByIdLessThanOrderByIdDesc(Long cursorId, Pageable pageable);

	List<Product> findAllByOrderByStartDateAsc(Pageable pageable);

	List<Product> findAllByIdLessThanOrderByStartDateAsc(Long cursorId, Pageable pageable);

}
