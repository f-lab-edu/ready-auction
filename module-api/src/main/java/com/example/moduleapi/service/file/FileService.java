package com.example.moduleapi.service.file;

import com.example.moduleapi.controller.response.ImageResponse;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductImage;
import com.example.moduledomain.domain.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<ProductImage> uploadImages(User user, Product product, List<MultipartFile> images);

    List<ImageResponse> loadImages(List<ProductImage> productImages);

    List<ProductImage> updateImages(User user, Product product, List<ProductImage> productImages,
                                    List<MultipartFile> images);

    void deleteImages(List<ProductImage> productImages);

}
