package com.example.moduleapi.fixture;

import com.example.moduledomain.common.response.ImageResponse;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class ImagesFixtures {
    public static String 이미지_이름 = "이미지 이름";
    public static String 원본_이미지_이름 = "original_name";
    public static String 이미지_경로 = "/images/products/test.jpg";

    public static List<ImageResponse> 이미지_응답 = Arrays.asList(new ImageResponse(1L, 원본_이미지_이름, 이미지_경로));
    public static MockMultipartFile mockMultipartFile = new MockMultipartFile(이미지_이름, 원본_이미지_이름, MediaType.IMAGE_JPEG_VALUE, 원본_이미지_이름.getBytes());

    public static MockMultipartFile 비어있는_이미지 = new MockMultipartFile("images", "", "image/jpeg", new byte[0]);
    public static List<MockMultipartFile> mockMultipartFiles = Arrays.asList(
            new MockMultipartFile("images", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "이미지 데이터 1".getBytes(StandardCharsets.UTF_8)),
            new MockMultipartFile("images", "image2.jpg", MediaType.IMAGE_JPEG_VALUE, "이미지 데이터 2".getBytes(StandardCharsets.UTF_8))
    );
}
