package com.example.moduleapi.service.product

import com.example.moduleapi.fixture.product.ProductFixtures
import com.example.moduledomain.domain.product.ProductImage
import com.example.moduledomain.repository.product.ProductImageRepository
import spock.lang.Specification

class ProductImageServiceTest extends Specification {
    ProductImageRepository productImageRepository = Mock()
    ProductImageService productImageService = new ProductImageService(productImageRepository)

    def "이미지 저장"() {
        given:
        List<ProductImage> productImages = [ProductFixtures.createProductImage()]

        when:
        productImageService.saveImage(productImages)

        then:
        1 * productImageRepository.saveAll(_)
    }

    def "이미지 조회"() {
        given:
        List<ProductImage> productImages = [
                ProductFixtures.createProductImage(),
                ProductFixtures.createProductImage(),
                ProductFixtures.createProductImage(),
        ]

        productImageRepository.findByProductId(1L) >> productImages

        when:
        def response = productImageService.getImage(1L)

        then:
        response.size() == 3
    }

    def "이미지 수정"() {
        given:
        List<ProductImage> productImages = [
                ProductFixtures.createProductImage(["productId": 1L]),
                ProductFixtures.createProductImage(["productId": 1L]),
                ProductFixtures.createProductImage(["productId": 1L]),
        ]

        productImageRepository.findByProductId(1L) >> productImages

        when:
        productImageService.updateImage(1L, productImages)

        then:
        //1 * productImageService.deleteImage(_)
        //1 * productImageService.saveImage(_)
        3 * productImageRepository.deleteById(_)
        1 * productImageRepository.saveAll(_)
    }

    def "이미지 삭제"() {
        given:
        List<ProductImage> productImages = [
                ProductFixtures.createProductImage(["productId": 1L]),
                ProductFixtures.createProductImage(["productId": 1L]),
                ProductFixtures.createProductImage(["productId": 1L]),
        ]

        productImageRepository.findByProductId(1L) >> productImages

        when:
        productImageService.deleteImage(1L)

        then:
        3 * productImageRepository.deleteById(_)
    }
}
