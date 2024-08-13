package com.example.readyauction.service.file;

import com.example.readyauction.controller.response.ImageResponse;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<ProductImage> uploadImages(String userId, List<MultipartFile> files, Product product);

    List<ImageResponse> loadImages(List<ProductImage> productImages);

    void delete(List<ImageResponse> imageResponses);

}
