package com.example.moduleapi.service.file

import com.example.moduleapi.fixture.UserFixtures.UserFixtures
import com.example.moduleapi.fixture.product.ProductFixtures
import com.example.moduledomain.domain.product.Product
import com.example.moduledomain.domain.user.User
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

class LocalFileServiceTest extends Specification {
    FileService localFileService = new LocalFileService()
    Path FileBaseUrl = Paths.get("fileTest/base/path")

    def setup() {
        Files.createDirectories(FileBaseUrl)
    }

    def cleanup() {
        if (Files.exists(FileBaseUrl)) {
            Files.walk(FileBaseUrl)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList())
                    .forEach { Path -> Files.deleteIfExists(Path) }
        }

        Files.walk(Paths.get("fileTest"))
                .filter(Files::isDirectory)  // 디렉토리만 필터링
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList())
                .forEach { Path -> Files.deleteIfExists(Path) }

    }

    def "로컬 BaseUrl에 파일 저장"() {
        given:
        User user = UserFixtures.createUser()
        Product product = ProductFixtures.createProduct()
        product.id = 1L
        localFileService.baseUrl = FileBaseUrl

        def images = [
                ProductFixtures.createMockMultipartFile(),
                ProductFixtures.createMockMultipartFile()
        ]

        when:
        def response = localFileService.uploadImages(user, product, images)

        then:
        response.size() == 2
    }

    def "로컬 파일 가져오기"() {
        given:
        def productImages = [ProductFixtures.createProductImage()]

        when:
        def response = localFileService.loadImages(productImages)

        then:
        response.size() == 1
        response.get(0).originalName == productImages.get(0).originalName
        response.get(0).imagePath == productImages.get(0).imageFullPath
    }

    // 고민중..
    def "로컬 파일 수정"() {
        given:
        User user = UserFixtures.createUser()
        Product product = ProductFixtures.createProduct()
        product.id = 1L
        localFileService.baseUrl = FileBaseUrl
        def images = [
                ProductFixtures.createMockMultipartFile(),
                ProductFixtures.createMockMultipartFile()
        ]
        def productImages = [ProductFixtures.createProductImage()]

        when:
        def response = localFileService.updateImages(user, product, productImages, images)

        then:
        // 1 * localFileService.deleteImages(_) 이게 왜 호출이 안되는거지
        response.size() == 2
    }

    // 고민중..
    def "로컬 파일 삭제"() {
        given:
        def productImages = [ProductFixtures.createProductImage()]
        def imagesResponse = [ProductFixtures.createImageResponse()]
        localFileService.loadImages(productImages) >> imagesResponse

        when:
        localFileService.deleteImages(productImages)

        then:
        1 * Files.delete(_) // 0번 호출되었다고 뜸..
    }
}
