package com.example.readyauction.controller.product;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.readyauction.annotaion.LoginUser;
import com.example.readyauction.controller.request.product.ProductSaveRequest;
import com.example.readyauction.controller.request.product.ProductUpdateRequest;
import com.example.readyauction.controller.response.product.ProductDeleteResponse;
import com.example.readyauction.controller.response.product.ProductFindResponse;
import com.example.readyauction.controller.response.product.ProductSaveResponse;
import com.example.readyauction.controller.response.product.ProductUpdateResponse;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.service.product.ProductService;

@RestController
public class ProductApiController {

	private final ProductService productService;

	public ProductApiController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("/api/v1/products")
	public ProductSaveResponse enroll(
		@LoginUser User user,
		@RequestPart(name = "product") ProductSaveRequest productSaveRequest,
		@RequestPart(name = "Images") List<MultipartFile> files) {
		ProductSaveResponse productSaveResponse = productService.enroll(user, productSaveRequest, files);
		return productSaveResponse;
	}

	@GetMapping("/api/v1/products/{id}")
	public ProductFindResponse findById(@PathVariable Long id) {
		ProductFindResponse productFindResponse = productService.findById(id);
		return productFindResponse;
	}

	@PutMapping("/api/v1/products/{id}")
	public ProductUpdateResponse update(
		@LoginUser User user,
		@PathVariable Long id,
		@RequestPart(name = "product") ProductUpdateRequest productUpdateRequest,
		@RequestPart(name = "Images") List<MultipartFile> files) {
		ProductUpdateResponse productUpdateResponse = productService.update(user, id, productUpdateRequest, files);
		return productUpdateResponse;
	}

	@DeleteMapping("/api/v1/products/{id}")
	public ProductDeleteResponse delete(@PathVariable Long id) {
		ProductDeleteResponse productDeleteResponse = productService.delete(id);
		return productDeleteResponse;
	}
}
