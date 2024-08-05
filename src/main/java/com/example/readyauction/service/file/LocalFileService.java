package com.example.readyauction.service.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.readyauction.controller.response.ImageResponse;
import com.example.readyauction.domain.product.Product;
import com.example.readyauction.domain.product.ProductImage;
import com.example.readyauction.exception.file.CreateDirectoryFailException;
import com.example.readyauction.exception.file.DeleteImageFailException;
import com.example.readyauction.exception.file.ImageFileUploadFailException;
import com.example.readyauction.repository.ProductImageRepository;

@Service
@Profile("dev")
public class LocalFileService implements FileService {
	// dev일땐 로컬에 이미지 저장 같은 방식으로.
	// 운영 서버일때는 S3 이런걸로 할 수 있도록 프로파일링 하기

	@Value("${ready.auction.image.file.base.url}")
	private String baseUrl;

	private final ProductImageRepository productImageRepository;

	public LocalFileService(ProductImageRepository productImageRepository) {
		this.productImageRepository = productImageRepository;
	}

	@Override
	public ProductImage uploadImage(String userId, MultipartFile file) {
		ProductImage productImage = createProductImage(file, userId);
		isExistDirectory(userId);

		try {
			file.transferTo(new File(productImage.getImageFullPath()));
			productImageRepository.save(productImage);
			return productImage;
		} catch (IOException e) {
			throw new ImageFileUploadFailException(e);
		}
	}

	@Override
	public List<ProductImage> uploadImages(String userId, List<MultipartFile> files) {
		List<ProductImage> productImages = new ArrayList<>();
		isExistDirectory(userId);

		for (MultipartFile file : files) {
			ProductImage productImage = createProductImage(file, userId);
			try {
				file.transferTo(new File(productImage.getImageFullPath()));
				productImages.add(productImage);
			} catch (IOException e) {
				throw new ImageFileUploadFailException(e);
			}
		}
		productImageRepository.saveAll(productImages);
		return productImages;
	}

	@Override
	public List<ImageResponse> loadImages(Product product) {
		List<ProductImage> productImages = productImageRepository.findByProduct(product);
		List<ImageResponse> imageResponses = productImages.stream()
			.map(productImage -> new ImageResponse(
				productImage.getOriginalName(),
				productImage.getImageFullPath()
			))
			.collect(Collectors.toList());
		return imageResponses;
	}

	@Override
	public void deleteImagesLocal(List<ImageResponse> imageResponses) {
		for (ImageResponse imageResponse : imageResponses) {
			Path path = Path.of(imageResponse.getImagePath());
			try {
				Files.delete(path);
			} catch (IOException e) {
				throw new DeleteImageFailException(e);
			}
		}
	}

	@Override
	public void deleteImagesDB(Product product) {
		productImageRepository.deleteByProduct(product);
	}

	private ProductImage createProductImage(MultipartFile file, String userId) {
		String originalFileName = file.getOriginalFilename();
		String savedFileName = createNewImageFileName(originalFileName);
		String SavedImageFullPath = createSavedImageFullPath(userId, savedFileName);
		return new ProductImage(originalFileName, savedFileName, SavedImageFullPath);
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
		StringBuilder fullDirPath = new StringBuilder()
			.append(baseUrl)
			.append(File.separator)
			.append(userId)
			.append(File.separator)
			.append(savedImageFileName);
		return String.valueOf(fullDirPath);
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
