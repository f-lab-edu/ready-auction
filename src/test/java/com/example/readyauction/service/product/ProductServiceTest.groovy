package com.example.readyauction.service.product

import com.example.readyauction.controller.request.product.ProductSaveRequest
import com.example.readyauction.controller.request.product.ProductUpdateRequest
import com.example.readyauction.domain.product.Product
import com.example.readyauction.domain.product.Status
import com.example.readyauction.domain.user.User
import com.example.readyauction.exception.product.UnauthorizedProductAccessException
import com.example.readyauction.repository.ProductRepository
import spock.lang.Specification

import java.time.LocalDateTime

class ProductServiceTest extends Specification {
    final USER_ID = "테스트"

    final PRODUCT_NAME = "상품 이름"
    final PRODUCT_DESCRIPTION = "상품 설명"
    final PRODUCT_START_DATE = LocalDateTime.now().plusDays(1)
    final PRODUCT_CLOSE_DATE = LocalDateTime.now().plusDays(5)
    final PRODUCT_START_PRICE = 5000
    final PENDING = Status.PENDING
    final ACTIVE = Status.ACTIVE
    final DONE = Status.DONE

    final UPDATE_PRODUCT_NAME = "수정된 상품 이름"
    final UPDATE_PRODUCT_DESCRIPTION = "수정된 상품 설명"
    final UPDATE_PRODUCT_START_DATE = LocalDateTime.now().plusDays(10)
    final UPDATE_PRODUCT_CLOSE_DATE = LocalDateTime.now().plusDays(20)
    final UPDATE_PRODUCT_START_PRICE = 10000

    ProductRepository productRepository = Mock(ProductRepository)
    ProductService productService = new ProductService(productRepository)

    User user = User.builder()
            .userId(USER_ID)
            .name("테스트")
            .encodedPassword("pwd")
            .build()

    def "경매 상품 등록"() {
        given:
        ProductSaveRequest request = new ProductSaveRequest(
                USER_ID,
                PRODUCT_NAME,
                PRODUCT_DESCRIPTION,
                PRODUCT_START_DATE,
                PRODUCT_CLOSE_DATE,
                PRODUCT_START_PRICE
        )
        Product product = Product.builder()
                .userId(USER_ID)
                .productName(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .startDate(PRODUCT_START_DATE)
                .closeDate(PRODUCT_CLOSE_DATE)
                .startPrice(PRODUCT_START_PRICE)
                .status(Status.PENDING)
                .build()
        product.id = 1L

        productRepository.save(_) >> product // _를 언제해야하는지 잘 모르겠음. 질문 ㄱ

        when:
        def response = productService.enroll(request)

        then:
        response.id == product.id
    }

    def "특정 id 경매 상품 조회"() {
        given:
        Product product = Product.builder()
                .userId(USER_ID)
                .productName(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .startDate(PRODUCT_START_DATE)
                .closeDate(PRODUCT_CLOSE_DATE)
                .startPrice(PRODUCT_START_PRICE)
                .status(Status.PENDING)
                .build()
        product.id = 1L
        productRepository.save(_) >> product
        productRepository.findById(product.getId()) >> Optional.of(product)

        when:
        def response = productService.findById(product.getId())

        then:
        response.productName == PRODUCT_NAME
        response.description == PRODUCT_DESCRIPTION
        response.startDate == PRODUCT_START_DATE
        response.closeDate == PRODUCT_CLOSE_DATE
        response.startPrice == PRODUCT_START_PRICE
        response.status == PENDING

    }

    def "경매 상품 정보만 수정"() {
        given:
        Product product = Product.builder()
                .userId(USER_ID)
                .productName(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .startDate(PRODUCT_START_DATE)
                .closeDate(PRODUCT_CLOSE_DATE)
                .startPrice(PRODUCT_START_PRICE)
                .status(Status.PENDING)
                .build()
        product.id = 1L
        productRepository.save(product)

        productRepository.findById(product.getId()) >> Optional.of(product)

        ProductUpdateRequest request = new ProductUpdateRequest(
                UPDATE_PRODUCT_NAME,
                UPDATE_PRODUCT_DESCRIPTION,
                UPDATE_PRODUCT_START_DATE,
                UPDATE_PRODUCT_CLOSE_DATE,
                UPDATE_PRODUCT_START_PRICE
        )

        when:
        def response = productService.update(user, product.getId(), request)

        then:
        response.productName == UPDATE_PRODUCT_NAME
        response.description == UPDATE_PRODUCT_DESCRIPTION
        response.startDate == UPDATE_PRODUCT_START_DATE
        response.closeDate == UPDATE_PRODUCT_CLOSE_DATE
        response.startPrice == UPDATE_PRODUCT_START_PRICE
        response.status == PENDING
    }

    def "내가 등록한 경매 상품이 아닐때 수정하면 예외 발생"() {
        given:
        User user = User.builder()
                .userId("HELLO")
                .name("테스트")
                .encodedPassword("pwd")
                .build()

        Product product = Product.builder()
                .userId(USER_ID)
                .productName(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .startDate(PRODUCT_START_DATE)
                .closeDate(PRODUCT_CLOSE_DATE)
                .startPrice(PRODUCT_START_PRICE)
                .status(Status.PENDING)
                .build()
        product.id = 1L
        productRepository.save(_) >> product
        productRepository.findById(product.getId()) >> Optional.of(product)

        ProductUpdateRequest request = new ProductUpdateRequest(
                UPDATE_PRODUCT_NAME,
                UPDATE_PRODUCT_DESCRIPTION,
                UPDATE_PRODUCT_START_DATE,
                UPDATE_PRODUCT_CLOSE_DATE,
                UPDATE_PRODUCT_START_PRICE
        )

        when:
        productService.update(user, product.getId(), request)

        then:
        def e = thrown(UnauthorizedProductAccessException)
    }

    def "경매 상품 상태가 대기중이 아닐때 수정하면 예외 발생"() {
        given:
        User user = User.builder()
                .userId("HELLO")
                .name("테스트")
                .encodedPassword("pwd")
                .build()

        Product product = Product.builder()
                .userId(USER_ID)
                .productName(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .startDate(PRODUCT_START_DATE)
                .closeDate(PRODUCT_CLOSE_DATE)
                .startPrice(PRODUCT_START_PRICE)
                .status(ACTIVE)
                .build()
        product.id = 1L
        productRepository.save(_) >> product
        productRepository.findById(product.getId()) >> Optional.of(product)

        ProductUpdateRequest request = new ProductUpdateRequest(
                UPDATE_PRODUCT_NAME,
                UPDATE_PRODUCT_DESCRIPTION,
                UPDATE_PRODUCT_START_DATE,
                UPDATE_PRODUCT_CLOSE_DATE,
                UPDATE_PRODUCT_START_PRICE
        )

        when:
        productService.update(user, product.getId(), request)

        then:
        def e = thrown(UnauthorizedProductAccessException)
    }

    def "내가 등록한 경매 상품이면 삭제"() {
        given:
        User user = User.builder()
                .userId(USER_ID)
                .name("테스트")
                .encodedPassword("pwd")
                .build()

        Product product = Product.builder()
                .userId(USER_ID)
                .productName(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .startDate(PRODUCT_START_DATE)
                .closeDate(PRODUCT_CLOSE_DATE)
                .startPrice(PRODUCT_START_PRICE)
                .status(PENDING)
                .build()
        product.id = 1L
        productRepository.save(_) >> product

        productRepository.findById(product.getId()) >> Optional.of(product)

        when:
        def response = productService.delete(user, product.getId())

        then:
        response == product.getId()
    }

    def "내가 등록한 경매 상품이 아닐시 삭제하면 예외 발생"() {
        given:
        User user = User.builder()
                .userId("HELLO")
                .name("테스트")
                .encodedPassword("pwd")
                .build()

        Product product = Product.builder()
                .userId(USER_ID)
                .productName(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .startDate(PRODUCT_START_DATE)
                .closeDate(PRODUCT_CLOSE_DATE)
                .startPrice(PRODUCT_START_PRICE)
                .status(PENDING)
                .build()
        product.id = 1L
        productRepository.save(product)

        productRepository.findById(product.getId()) >> Optional.of(product)

        when:
        productService.delete(user, product.getId())

        then:
        def e = thrown(UnauthorizedProductAccessException)
    }


    def "경매 상품 상태가 대기중이 아니면 접근하면 예외 발생"() {
        given:
        User user = User.builder()
                .userId("HELLO")
                .name("테스트")
                .encodedPassword("pwd")
                .build()

        Product product = Product.builder()
                .userId(USER_ID)
                .productName(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .startDate(PRODUCT_START_DATE)
                .closeDate(PRODUCT_CLOSE_DATE)
                .startPrice(PRODUCT_START_PRICE)
                .status(ACTIVE)
                .build()
        product.id = 1L
        productRepository.save(product)

        productRepository.findById(product.getId()) >> Optional.of(product)

        when:
        productService.delete(user, product.getId())

        then:
        def e = thrown(UnauthorizedProductAccessException)
    }


}
