package com.example.readyauction.service.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.readyauction.domain.product.ProductImage;
import com.example.readyauction.repository.ProductImageRepository;

@Service
public class ProductImageService {

	private final ProductImageRepository productImageRepository;

	public ProductImageService(ProductImageRepository productImageRepository) {
		this.productImageRepository = productImageRepository;
	}

	@Transactional
	public void saveImage(List<ProductImage> productImages) {
		productImageRepository.saveAll(productImages);
	}

	@Transactional
	public List<ProductImage> getImage(Long productId) {
		List<ProductImage> productImages = productImageRepository.findByProductId(productId);
		return productImages;
	}

	@Transactional
	public void updateImage(Long productId, List<ProductImage> productImages) {
		this.deleteImage(productId);
		this.saveImage(productImages);
	}

	@Transactional
	public List<ProductImage> deleteImage(Long productId) {
		List<ProductImage> productImages = productImageRepository.findByProductId(productId);
		for (ProductImage productImage : productImages) {
			productImageRepository.deleteById(productImage.getId());
		}
		return productImages;

	}
}
