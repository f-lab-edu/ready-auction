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
        response.size() == 2
    }

    def "로컬 파일 삭제"() {
        given:
        def productImages = [
                ProductFixtures.createProductImage(imageFullPath: "fileTest/base/path/test-image.jpg")
        ]
        def imagesResponse = [ProductFixtures.createImageResponse(
                imagePath: "fileTest/base/path/test-image.jpg"
        )]
        localFileService.loadImages(productImages) >> imagesResponse
        // 테스트 전에 파일을 실제로 생성
        def testFile = FileBaseUrl.resolve("test-image.jpg")
        println(testFile)
        if (!Files.exists(FileBaseUrl)) {
            Files.createDirectories(FileBaseUrl)  // 디렉토리가 없으면 생성
        }
        if (!Files.exists(testFile)) {
            Files.createFile(testFile)  // 더미 파일 생성
        }

        when:
        localFileService.deleteImages(productImages)

        then:
        !Files.exists(testFile)
        def remainingFiles = Files.walk(FileBaseUrl)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList())
        remainingFiles.size() == 0
    }
}
