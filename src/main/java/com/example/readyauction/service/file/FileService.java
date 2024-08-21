package com.example.readyauction.service.file;

import com.example.readyauction.controller.response.ImageResponse;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductImage;
import com.example.readyauction.domain.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<ProductImage> uploadImages(User user, Product product, List<MultipartFile> images);

    List<ImageResponse> loadImages(List<ProductImage> productImages);

    List<ProductImage> updateImages(User user, Product product, List<ProductImage> productImages, List<MultipartFile> images);

    void deleteImages(List<ProductImage> productImages);

}
