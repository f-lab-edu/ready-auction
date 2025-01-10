package com.example.moduleapi.service.file;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductImage;
import com.example.moduledomain.domain.user.User;
import com.example.moduledomain.response.ImageResponse;

public interface FileService {
    List<ProductImage> uploadImages(User user, Product product, List<MultipartFile> images);

    List<ImageResponse> loadImages(List<ProductImage> productImages);

    List<ProductImage> updateImages(User user, Product product, List<ProductImage> productImages,
                                    List<MultipartFile> images);

    void deleteImages(List<ProductImage> productImages);

}
