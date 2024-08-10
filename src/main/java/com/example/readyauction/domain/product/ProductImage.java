package com.example.readyauction.domain.product;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "product_Image")
@Getter
public class ProductImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "product_id", nullable = false, updatable = false)
	private Product product;

	@Column(name = "originalName", nullable = false)
	private String originalName;

	@Column(name = "savedName", nullable = false)
	private String savedName;

	@Column(name = "imageFullPath", nullable = false)
	private String imageFullPath;

	@Builder
	public ProductImage(String originalName, String savedName, String imageFullPath) {
		this.originalName = originalName;
		this.savedName = savedName;
		this.imageFullPath = imageFullPath;
	}

	public void mappingProduct(Product product) {
		this.product = product;
	}
}
