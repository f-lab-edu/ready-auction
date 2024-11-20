package com.example.moduleapi.service.product

import com.example.moduleapi.controller.request.product.ProductSaveRequest
import com.example.moduleapi.controller.request.product.ProductUpdateRequest
import com.example.moduleapi.controller.response.ImageResponse
import com.example.moduleapi.exception.product.NotFoundProductException
import com.example.moduleapi.fixture.UserFixtures.UserFixtures
import com.example.moduleapi.fixture.product.ProductFixtures
import com.example.moduleapi.service.file.FileService
import com.example.moduledomain.domain.product.OrderBy
import com.example.moduledomain.domain.product.Product
import com.example.moduledomain.domain.product.ProductImage
import com.example.moduledomain.domain.user.User
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

class ProductFacadeTest extends Specification {
    FileService fileService = Mock()
    ProductImageService productImageService = Mock()
    ProductService productService = Mock()
    ProductLikeService productLikeService = Mock()
    def productFacade = new ProductFacade(fileService, productImageService, productService, productLikeService)

    def "상품 등록 성공"() {
        given:
        User user = UserFixtures.createUser()
        ProductSaveRequest request = new ProductSaveRequest(
                userId: "test",
                productName: "test",
                description: "test",
                startDate: ProductFixtures.시작일,
                closeDate: ProductFixtures.종료일,
                startPrice: 1000
        )

        def multipartImages = [ProductFixtures.createMockMultipartFile()]
        Product product = ProductFixtures.createProduct()
        List<ProductImage> productImages = [ProductFixtures.createProductImage()]

        when:
        def response = productFacade.enroll(user, request, multipartImages)

        then:
        1 * productService.enroll(_ as ProductSaveRequest) >> product
        1 * fileService.uploadImages(_ as User, _ as Product, _ as List) >> productImages
        1 * productImageService.saveImage(productImages)
    }

    def "상품 등록 실패 - 유효성 검증 실패"() {
        given:
        User user = UserFixtures.createUser()

        ProductSaveRequest request = new ProductSaveRequest(
                userId: "test",
                productName: productName,
                description: description,
                startDate: startDate,
                closeDate: closeDate,
                startPrice: startPrice
        )

        List<MultipartFile> multipartImages = images

        when:
        productFacade.enroll(user, request, multipartImages)

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        productName | description | startDate           | closeDate           | startPrice || images        || expected
        ""          | "test"      | ProductFixtures.시작일 | ProductFixtures.종료일 | 1000       || ["image.jpg"] || "상품명은 필수 값입니다."
        "product"   | ""          | ProductFixtures.시작일 | ProductFixtures.종료일 | 1000       || ["image.jpg"] || "상품 설명은 필수 값입니다."
        "product"   | "test"      | ProductFixtures.과거일 | ProductFixtures.종료일 | 1000       || ["image.jpg"] || "경매 시작일이 현재 시각보다 이전일 수 없습니다."
        "product"   | "test"      | ProductFixtures.시작일 | ProductFixtures.과거일 | 1000       || ["image.jpg"] || "경매 종료일이 경매 시작일보다 이전일 수 없습니다."
        "product"   | "test"      | ProductFixtures.시작일 | ProductFixtures.종료일 | 999        || ["image.jpg"] || "시작 가격은 최소 1000원입니다."
        "product"   | "test"      | ProductFixtures.시작일 | ProductFixtures.종료일 | 1000       || null          || "경매 상품에 대한 이미지는 필수 값입니다."
        "product"   | "test"      | ProductFixtures.시작일 | ProductFixtures.종료일 | 1000       || []            || "경매 상품에 대한 이미지는 필수 값입니다."
    }

    def "상품 조회 성공"() {
        given:
        Product product = ProductFixtures.createProduct()
        ProductImage productImage = ProductFixtures.createProductImage()
        List<ProductImage> productImages = [productImage]
        List<ImageResponse> imageResponse = [ProductFixtures.createImageResponse()]

        productService.findById(1L) >> product
        productImageService.getImage(1L) >> productImages
        fileService.loadImages(_) >> imageResponse

        when:
        def response = productFacade.findById(1L)

        then:
        response.productName == product.productName
        response.description == product.description
        response.startDate == product.startDate
        response.closeDate == product.closeDate
        response.startPrice == product.startPrice
        response.imageResponses.size() == 1
        response.imageResponses[0].originalName == productImage.originalName
        response.imageResponses[0].imagePath == productImage.imageFullPath

    }

    def "상품 조회 실패 - 존재하지 않는 상품"() {
        given:
        productService.findById(1L) >> { throw new NotFoundProductException(1L) }
        productImageService.getImage(1L) >> null
        fileService.loadImages(_) >> null

        when:
        def response = productFacade.findById(1L)

        then:
        def e = thrown(NotFoundProductException.class)
        e.message == 1L + ": 존재하지 않는 상품입니다."

    }

    def "상품 목록 조회"() {
        given:
        String keyword = "test"
        int pageNo = 1
        int pageSize = 10
        OrderBy orderBy = OrderBy.LATEST
        List<Product> products = [
                ProductFixtures.createProduct(["productName": "productName1"]),
                ProductFixtures.createProduct(["productName": "productName2"]),
                ProductFixtures.createProduct(["productName": "productName3"])
        ]

        when:
        def response = productFacade.findAll(keyword, ProductFixtures.READY, pageNo, pageSize, orderBy)

        then:
        1 * productService.findProductWithCriteria(keyword, ProductFixtures.READY, pageNo, pageSize, orderBy) >> products
        response.pageNo == pageNo
        response.items.size() == 3

        response.items[0].productName == "productName1"
        response.items[1].productName == "productName2"
        response.items[2].productName == "productName3"

    }

    def "상품 수정 성공"() {
        given:
        User user = UserFixtures.createUser()
        ProductUpdateRequest request = new ProductUpdateRequest(
                productName: "update test",
                description: "update test",
                startDate: ProductFixtures.시작일,
                closeDate: ProductFixtures.종료일,
                startPrice: 2000
        )
        Product product = ProductFixtures.createProduct(["productName": "update test", "description": "update test"])
        ProductImage productImage = ProductFixtures.createProductImage()
        MockMultipartFile multipartFile = ProductFixtures.createMockMultipartFile()

        List<ProductImage> productImages = [productImage]
        List<MockMultipartFile> multipartFiles = [multipartFile]

        when:
        productFacade.update(user, 1L, request, multipartFiles)

        then:
        1 * productService.update(user, 1L, request) >> product
        1 * productImageService.getImage(1L) >> productImages
        1 * fileService.updateImages(user, product, productImages, multipartFiles) >> productImages
        1 * productImageService.updateImage(1L, productImages)
    }

    def "상품 수정 실패 - 입력값 유효성 검증 실패"() {
        given:
        User user = UserFixtures.createUser()

        ProductUpdateRequest request = new ProductUpdateRequest(
                productName: productName,
                description: description,
                startDate: startDate,
                closeDate: closeDate,
                startPrice: startPrice
        )

        List<MultipartFile> multipartImages = images

        when:
        productFacade.update(user, 1L, request, multipartImages)

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        productName | description | startDate           | closeDate           | startPrice || images        || expected
        ""          | "test"      | ProductFixtures.시작일 | ProductFixtures.종료일 | 1000       || ["image.jpg"] || "상품명은 필수 값입니다."
        "product"   | ""          | ProductFixtures.시작일 | ProductFixtures.종료일 | 1000       || ["image.jpg"] || "상품 설명은 필수 값입니다."
        "product"   | "test"      | ProductFixtures.과거일 | ProductFixtures.종료일 | 1000       || null          || "경매 시작일이 현재 시각보다 이전일 수 없습니다."
        "product"   | "test"      | ProductFixtures.시작일 | ProductFixtures.과거일 | 1000       || null          || "경매 종료일이 경매 시작일보다 이전일 수 없습니다."
        "product"   | "test"      | ProductFixtures.시작일 | ProductFixtures.종료일 | 999        || null          || "시작 가격은 최소 1000원입니다."
    }

    def "상품 삭제 성공"() {
        given:
        User user = UserFixtures.createUser()
        def productImages = [ProductFixtures.createProductImage()]

        when:
        def response = productFacade.delete(user, 1L)

        then:
        1 * productService.delete(user, 1L) >> 1L
        1 * productImageService.deleteImage(1L) >> productImages
        1 * fileService.deleteImages(productImages)
        response.id == 1L
    }
}