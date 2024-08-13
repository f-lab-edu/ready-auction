package com.example.readyauction.service.file;

import com.example.readyauction.controller.response.ImageResponse;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductImage;
import com.example.readyauction.exception.file.CreateDirectoryFailException;
import com.example.readyauction.exception.file.DeleteImageFailException;
import com.example.readyauction.exception.file.ImageFileUploadFailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Profile("dev")
public class LocalFileService implements FileService {
	/*
		dev : 개발환경일 땐 로컬에 이미지 저장.
		prod : 운영 환경일 때는 S3에 이미지 저장.
	*/

    @Value("${ready.auction.image.file.base.url}")
    private String baseUrl;

    @Override
    public List<ProductImage> uploadImages(String userId, List<MultipartFile> files, Product product) {
        List<ProductImage> productImages = new ArrayList<>();
        isExistDirectory(userId);

        for (MultipartFile file : files) {
            ProductImage productImage = createProductImage(file, userId, product);
            saveImage(file, productImage.getImageFullPath());
            productImages.add(productImage);
        }
        return productImages;
    }

    @Override
    public List<ImageResponse> loadImages(List<ProductImage> productImages) {
        List<ImageResponse> imageResponses = productImages.stream()
                .map(ImageResponse::from)
                .collect(Collectors.toList());
        return imageResponses;
    }

    @Override
    public void delete(List<ImageResponse> imageResponses) {
        for (ImageResponse imageResponse : imageResponses) {
            Path path = Path.of(imageResponse.getImagePath());
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new DeleteImageFailException(e);
            }
        }
    }

    private void saveImage(MultipartFile file, String path) {
        try {
            file.transferTo(new File(path));
        } catch (IOException e) {
            throw new ImageFileUploadFailException(e);
        }
    }

    private ProductImage createProductImage(MultipartFile file, String userId, Product product) {
        String originalFileName = file.getOriginalFilename();
        String savedFileName = createNewImageFileName(originalFileName);
        String savedImageFullPath = createSavedImageFullPath(userId, savedFileName);
        return new ProductImage(product.getId(), originalFileName, savedFileName, savedImageFullPath);
    }

    private String createNewImageFileName(String originalImageFile) {
        String uuid = UUID.randomUUID().toString();
        String extension = StringUtils.getFilenameExtension(originalImageFile);
        StringBuilder newName = new StringBuilder()
                .append(uuid)
                .append(".")
                .append(extension);
        return String.valueOf(newName);
    }

    private String createSavedImageFullPath(String userId, String savedImageFileName) {
        StringBuilder fullDirectoryPath = new StringBuilder()
                .append(baseUrl)
                .append(File.separator)
                .append(userId)
                .append(File.separator)
                .append(savedImageFileName);
        return String.valueOf(fullDirectoryPath);
    }

    private void isExistDirectory(String userId) {
        Path dirPath = Paths.get(baseUrl, userId);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new CreateDirectoryFailException(e);
            }
        }
    }

}
