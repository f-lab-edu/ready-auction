package com.example.readyauction.service.product

import com.example.readyauction.controller.request.product.ProductSaveRequest
import com.example.readyauction.controller.request.product.ProductUpdateRequest
import com.example.readyauction.controller.response.ImageResponse
import com.example.readyauction.domain.product.Product
import com.example.readyauction.domain.product.ProductImage
import com.example.readyauction.domain.product.Status
import com.example.readyauction.domain.user.User
import com.example.readyauction.service.file.FileService
import org.springframework.mock.web.MockMultipartFile
import spock.lang.Specification

import java.time.LocalDateTime

class ProductFacadeTest extends Specification {

    FileService fileService = Mock()
    ProductImageService productImageService = Mock()
    ProductService productService = Mock()
    def productFacade = new ProductFacade(fileService, productImageService, productService)

    def "상품 등록 성공"() {
        given:
        User user = createUser("TEST")
        ProductSaveRequest productSaveRequest = createProductSaveRequest("TEST")
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
        def multipartImages = [mockMultipartFile1, mockMultipartFile2]
        Product product = new Product(id: 1L)
        def productImages = [new ProductImage()]

        productService.enroll(productSaveRequest) >> product
        fileService.uploadImages(user, product, multipartImages) >> productImages
        when:
        def response = productFacade.enroll(user, productSaveRequest, multipartImages)
        then:
        response.id == product.getId()
    }

    def "상품 조회 성공"() {
        given:
        Long productId = 1L
        Product product = createProduct("TEST");
        def productImages = [new ProductImage()]
        def imageResponses = [new ImageResponse()]

        productService.findById(productId) >> product
        productImageService.getImage(productId) >> productImages
        fileService.loadImages(productImages) >> imageResponses
        when:
        def response = productFacade.findById(productId)

        then:
        // 이 부분 좀 추가해야겠음.
        response.userId == "TEST"
    }

    def "상품 수정 성공"() {
        given:
        String userId = "TEST"
        def multipartImages = [MockMultipartFile]
        Long productId = 1L
        User user = createUser(userId)
        ProductUpdateRequest productUpdateRequest = createProductUpdateRequest()
        Product product = createProduct(userId);

        def productImages = [new ProductImage()]
        def newProductImages = [new ProductImage()]

        productService.update(user, productId, productUpdateRequest) >> product
        productImageService.getImage(productId) >> productImages
        fileService.updateImages(user, product, multipartImages) >> newProductImages
        productImageService.updateImage(productId, newProductImages)

        when:
        def response = productFacade.update(user, productId, productUpdateRequest, multipartImages)

        then:
        response.id == productId
    }

    def "상품 삭제 성공"() {
        given:
        Long productId = 1L
        String userId = "TEST"
        User user = createUser(userId)
        def productImages = [new ProductImage()]

        productService.delete(user, productId) >> productId
        productImageService.deleteImage(productId) >> productImages
        fileService.deleteImages(productImages)
        when:
        def response = productFacade.delete(user, productId)
        then:
        response.id == productId
    }

    private User createUser(String userId) {
        return User.builder()
                .userId(userId)
                .name("테스트")
                .encodedPassword("pwd")
                .build()
    }

    private ProductSaveRequest createProductSaveRequest(String userId) {
        return ProductSaveRequest.builder()
                .userId(userId)
                .productName("상품 이름")
                .description("상품 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .closeDate(LocalDateTime.now().plusDays(5))
                .startPrice(5000)
                .build()
    }

    private ProductUpdateRequest createProductUpdateRequest() {
        return ProductUpdateRequest.builder()
                .productName("상품 이름")
                .description("상품 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .closeDate(LocalDateTime.now().plusDays(5))
                .startPrice(5000)
                .build()
    }

    private Product createProduct(String userId) {
        return Product.builder()
                .userId(userId)
                .productName("상품 이름")
                .description("상품 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .closeDate(LocalDateTime.now().plusDays(5))
                .startPrice(5000)
                .status(Status.PENDING)
                .build()
    }


}
