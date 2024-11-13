package com.example.moduleapi.service.product

import com.example.moduleapi.controller.request.product.ProductSaveRequest
import com.example.moduleapi.controller.request.product.ProductUpdateRequest
import com.example.moduleapi.exception.product.ProductNotPendingException
import com.example.moduleapi.fixture.UserFixtures.UserFixtures
import com.example.moduleapi.fixture.product.ProductFixtures
import com.example.moduledomain.domain.product.OrderBy
import com.example.moduledomain.domain.product.Product
import com.example.moduledomain.domain.product.ProductCondition
import com.example.moduledomain.domain.user.User
import com.example.moduledomain.repository.product.ProductRepository
import spock.lang.Specification

class ProductServiceTest extends Specification {

    ProductRepository productRepository = Mock(ProductRepository)
    ProductService productService = new ProductService(productRepository)

    def "경매 상품 등록"() {
        given:
        ProductSaveRequest request = new ProductSaveRequest(
                userId: "test",
                productName: "test",
                description: "test",
                startDate: ProductFixtures.시작일,
                closeDate: ProductFixtures.종료일,
                startPrice: 1000
        )

        when:
        productService.enroll(request)

        then:
        1 * productRepository.save(_) >> {
            Product product = it[0]
            verifyAll(product) {
                assert it.userId == "test"
                assert it.productName == "test"
                assert it.description == "test"
                assert it.startPrice == 1000
                assert it.productCondition == ProductFixtures.READY
            }
        }
    }

    def "특정 id 경매 상품 조회"() {
        given:
        productRepository.findById(1L) >> Optional.of(ProductFixtures.createProduct())

        when:
        def response = productService.findById(1L)

        then:
        response.userId == "test"
        response.productName == "test"
        response.description == "test"
        response.startPrice == 1000
        response.productCondition == ProductFixtures.READY
    }


    def "상품 목록 조회"() {
        given:
        String keyword = ""
        int pageNo = 1
        int pageSize = 3
        OrderBy orderBy = OrderBy.LATEST

        List<Product> mockProducts = [
                ProductFixtures.createProduct([productCondition: ProductCondition.ACTIVE]),
                ProductFixtures.createProduct([productCondition: ProductCondition.ACTIVE]),
                ProductFixtures.createProduct([productCondition: ProductCondition.ACTIVE])
        ]

        productRepository.findProductsWithCriteria(keyword, ProductFixtures.ACTIVE, pageNo, pageSize, orderBy) >> mockProducts

        when:
        List<Product> result = productService.findProductWithCriteria(keyword, ProductFixtures.ACTIVE, pageNo, pageSize, orderBy)

        then:
        result.size() == 3
    }

    def "경매 상품 정보만 수정"() {
        given:
        ProductUpdateRequest request = new ProductUpdateRequest(
                productName: "update test",
                description: "update test",
                startDate: ProductFixtures.시작일,
                closeDate: ProductFixtures.종료일,
                startPrice: 2000
        )

        User user = UserFixtures.createUser()
        Product product = ProductFixtures.createProduct()
        product.id = 1L

        productRepository.findById(1L) >> Optional.of(product)

        when:
        def response = productService.update(user, 1L, request)

        then:
        // 이거 product 추적(?)을 못하는거같음.
        //1 * product.updateProductInfo(request.getProductName(), request.getDescription(), request.getStartPrice(), request.getCloseDate(), request.getStartPrice())
        response.productName == request.getProductName()
        response.description == request.getDescription()
        response.startDate == request.getStartDate()
        response.closeDate == request.getCloseDate()
        response.startPrice == request.getStartPrice()
    }

    def "경매 상품 상태가 대기중이 아닐때 수정하면 예외 발생"() {
        given:
        ProductUpdateRequest request = new ProductUpdateRequest(
                productName: "update test",
                description: "update test",
                startDate: ProductFixtures.시작일,
                closeDate: ProductFixtures.종료일,
                startPrice: 2000
        )

        User user = UserFixtures.createUser()
        Product product = ProductFixtures.createProduct([productCondition: ProductCondition.ACTIVE])
        product.id = 1L

        productRepository.findById(1L) >> Optional.of(product)

        when:
        def response = productService.update(user, 1L, request)

        then:
        then:
        def e = thrown(ProductNotPendingException.class)
        e.message == 1 + ": 상품의 상태가 대기중이 아닙니다."
    }

    def "경매 상품 삭제"() {
        given:
        User user = UserFixtures.createUser()
        Product product = ProductFixtures.createProduct()
        product.id = 1L

        productRepository.findById(1L) >> Optional.of(product)

        when:
        def response = productService.delete(user, 1L)

        then:
        response == 1L
    }


    def "경매 상품 상태가 대기중이 아닐떄 삭제 시 예외 발생"() {
        given:
        User user = UserFixtures.createUser()
        Product product = ProductFixtures.createProduct([productCondition: ProductCondition.ACTIVE])
        product.id = 1L

        productRepository.findById(1L) >> Optional.of(product)

        when:
        productService.delete(user, 1L)

        then:
        def e = thrown(ProductNotPendingException.class)
        e.message == 1 + ": 상품의 상태가 대기중이 아닙니다."
    }
}
