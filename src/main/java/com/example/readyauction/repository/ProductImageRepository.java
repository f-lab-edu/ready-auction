package com.example.readyauction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
	List<ProductImage> findByProduct(Product product);

	void deleteByProduct(Product product);

}
