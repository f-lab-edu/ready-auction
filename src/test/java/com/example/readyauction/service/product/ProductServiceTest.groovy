package com.example.readyauction.service.product

import com.example.readyauction.controller.request.product.ProductSaveRequest
import com.example.readyauction.controller.request.product.ProductUpdateRequest
import com.example.readyauction.domain.product.Product
import com.example.readyauction.domain.product.ProductImage
import com.example.readyauction.domain.product.Status
import com.example.readyauction.exception.product.UnauthorizedProductAccessException
import com.example.readyauction.repository.ProductImageRepository
import com.example.readyauction.repository.ProductRepository
import com.example.readyauction.service.file.FileService
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

import java.time.LocalDateTime

class ProductServiceTest extends Specification {

    final PRODUCT_NAME = "상품 이름"
    final PRODUCT_DESCRIPTION = "상품 설명"
    final UPDATE_PRODUCT_NAME = "수정된 상품 이름"
    final UPDATE_PRODUCT_DESCRIPTION = "수정된 상품 설명"
    String userId = "TEST"
    ProductRepository productRepository = Mock()
    ProductImageRepository productImageRepository = Mock()
    FileService fileService = Mock()
    ProductService productService = new ProductService(productRepository, productImageRepository, fileService)

    Product product = Product.builder()
            .userId(userId)
            .productName(PRODUCT_NAME)
            .description(PRODUCT_DESCRIPTION)
            .startDate(LocalDateTime.now().plusDays(1))
            .closeDate(LocalDateTime.now().plusDays(5))
            .startPrice(5000)
            .status(Status.PENDING)
            .build()

    MockMultipartFile mockMultipartFile1 = new MockMultipartFile(
            "productImage1",
            "originalProductImageFileName1",
            "image/jpg",
            "productImageFileName1".getBytes()
    )
    MockMultipartFile mockMultipartFile2 = new MockMultipartFile(
            "productImage2",
            "originalProductImageFileName2",
            "image/jpg",
            "productImageFileName2".getBytes()
    )

    ProductImage productImage1 = ProductImage.builder()
            .originalName("originalName1")
            .savedName("savedName1")
            .imageFullPath("path1")
            .build()

    ProductImage productImage2 = ProductImage.builder()
            .originalName("originalName2")
            .savedName("savedName2")
            .imageFullPath("path2")
            .build()

    def "경매 상품 등록"() {
        given:

        ProductSaveRequest request = new ProductSaveRequest(
                PRODUCT_NAME,
                PRODUCT_DESCRIPTION,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                5000
        )

        List<MultipartFile> images = new ArrayList<>()
        images.add(mockMultipartFile1)
        images.add(mockMultipartFile2)

        List<ProductImage> productImages = new ArrayList<>()
        productImages.add(productImage1)
        productImages.add(productImage2)

        fileService.uploadImages(userId, images) >> productImages
        product.id = 1L
        productRepository.save(_) >> product

        when:
        def response = productService.enroll(userId, request, images)

        then:
        response.id == product.getId()

    }

    def "특정 id 경매 상품 조회"() {
        given:
        productRepository.save(product)
        product.id = 1L
        productRepository.findById(product.getId()) >> Optional.of(product)

        when:
        def response = productService.findById(product.getId())

        then:
        response.productName == product.getProductName()
        response.description == product.getDescription()

    }

    def "경매 상품 정보만 수정"() {
        given:
        productRepository.save(product)
        product.id = 1L

        productRepository.findById(product.getId()) >> Optional.of(product)
        ProductUpdateRequest request = new ProductUpdateRequest(
                UPDATE_PRODUCT_NAME,
                UPDATE_PRODUCT_DESCRIPTION,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                105000
        )
        List<MultipartFile> images = new ArrayList<>()// 이 부분은 어떻게 해야하는거지

        when:
        def response = productService.update(userId, product.getId(), request, images)

        then:
        response.id == product.id
    }

    def "경매 상품 정보 및 이미지 수정"() {
        given:
        productRepository.save(product)
        product.id = 1L

        productRepository.findById(product.getId()) >> Optional.of(product)

        ProductUpdateRequest request = new ProductUpdateRequest(
                UPDATE_PRODUCT_NAME,
                UPDATE_PRODUCT_DESCRIPTION,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                105000
        )

        List<MultipartFile> images = new ArrayList<>()
        images.add(mockMultipartFile1)

        List<ProductImage> productImages = new ArrayList<>()
        productImages.add(productImage1)
        productImages.add(productImage2)

        productImageRepository.findByProductId(product.getId()) >> productImages
        fileService.loadImages(productImages) >> _
        fileService.uploadImages(images) >> _

        when:
        def response = productService.update(userId, product.getId(), request, images)

        then:
        response.id == product.id
    }

    def "내가 등록한 경매 상품이 아닌데 수정하면 예외 발생"() {
        given:
        String userId = "HELLO"
        productRepository.save(product)
        product.id = 1L

        productRepository.findById(product.getId()) >> Optional.of(product)
        ProductUpdateRequest request = new ProductUpdateRequest(
                UPDATE_PRODUCT_NAME,
                UPDATE_PRODUCT_DESCRIPTION,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                105000
        )
        List<MultipartFile> images = new ArrayList<>()// 이 부분은 어떻게 해야하는거지

        when:
        productService.update(userId, product.getId(), request, images)

        then:
        def e = thrown(UnauthorizedProductAccessException)
    }

    def "경매 상품 상태가 대기중이 아닐때 수정하면 예외 발생"() {
        given:
        String userId = "HELLO"
        product.status = Status.ACTIVE
        productRepository.save(product)
        product.id = 1L

        productRepository.findById(product.getId()) >> Optional.of(product)
        ProductUpdateRequest request = new ProductUpdateRequest(
                UPDATE_PRODUCT_NAME,
                UPDATE_PRODUCT_DESCRIPTION,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                105000
        )
        List<MultipartFile> images = new ArrayList<>()// 이 부분은 어떻게 해야하는거지

        when:
        productService.update(userId, product.getId(), request, images)

        then:
        def e = thrown(UnauthorizedProductAccessException)
    }

    def "내가 등록한 경매 상품이 아닐시 삭제하면 예외 발생"() {
        given:
        String userId = "HELLo"
        productRepository.save(product)
        product.id = 1L

        productRepository.findById(product.getId()) >> Optional.of(product)

        when:
        productService.delete(userId, product.getId())

        then:
        def e = thrown(UnauthorizedProductAccessException)
    }

    def "내가 등록한 경매 상품이면 삭제"() {
        given:
        productRepository.save(product)
        product.id = 1L

        productRepository.findById(product.getId()) >> Optional.of(product)

        List<ProductImage> productImages = new ArrayList<>()
        productImages.add(productImage1)
        productImages.add(productImage2)
        productImageRepository.findByProductId(product.getId()) >> productImages
        fileService.loadImages(productImages) >> _

        when:
        def response = productService.delete(userId, product.getId())
        then:
        response.id == product.getId()
    }

    def "경매 상품 상태가 대기중이 아니면 예외 발생"() {
        given:
        String userId = "HELLo"
        product.status = Status.ACTIVE
        productRepository.save(product)
        product.id = 1L

        productRepository.findById(product.getId()) >> Optional.of(product)

        when:
        productService.delete(userId, product.getId())

        then:
        def e = thrown(UnauthorizedProductAccessException)
    }


}
