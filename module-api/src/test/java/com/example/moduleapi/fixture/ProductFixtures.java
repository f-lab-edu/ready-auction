package com.example.moduleapi.fixture;

import com.example.moduleapi.controller.request.product.ProductSaveRequest;
import com.example.moduleapi.controller.request.product.ProductUpdateRequest;
import com.example.moduleapi.controller.response.PagingResponse;
import com.example.moduleapi.controller.response.product.ProductFindResponse;
import com.example.moduleapi.controller.response.product.ProductLikeResponse;
import com.example.moduleapi.controller.response.product.ProductResponse;
import com.example.moduleapi.exception.product.NotFoundProductException;
import com.example.moduleapi.exception.product.ProductNotPendingException;
import com.example.moduleapi.exception.product.UnauthorizedProductAccessException;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductCondition;

import java.time.LocalDateTime;
import java.util.List;

public class ProductFixtures {

    public static String 상품_이름 = "testProductName";
    public static String 상품_설명 = "Test Description";
    public static LocalDateTime 현재시간 = LocalDateTime.now();
    public static LocalDateTime 종료일 = 현재시간.plusDays(30);
    public static int 시작가격 = 50000;

    private static ProductSaveRequest createProductSaveRequest(String userId, String productName, String description,
                                                               LocalDateTime startDate, LocalDateTime closeDate, int startPrice) {
        return ProductSaveRequest.builder()
                .userId(userId)
                .productName(productName)
                .description(description)
                .startDate(startDate)
                .closeDate(closeDate)
                .startPrice(startPrice)
                .build();
    }

    private static ProductUpdateRequest createProductUpdateRequest(String productName, String description,
                                                                   LocalDateTime startDate, LocalDateTime closeDate, int startPrice) {
        return ProductUpdateRequest.builder()
                .productName(productName)
                .description(description)
                .startDate(startDate)
                .closeDate(closeDate)
                .startPrice(startPrice)
                .build();
    }

    public static Product 상품 = Product.builder()
            .productName(상품_이름)
            .userId(UserFixtures.유저_아이디)
            .description(상품_설명)
            .startDate(현재시간)
            .closeDate(종료일)
            .startPrice(시작가격)
            .productCondition(ProductCondition.READY)
            .build();

    public static ProductSaveRequest 상품_등록_요청_상품명_유효성_검증실패_요청 = createProductSaveRequest(
            UserFixtures.유저_아이디,
            "",  // 상품명 비워둠
            상품_설명,
            현재시간.plusDays(1),  // 경매 시작일
            현재시간.plusDays(2),  // 경매 종료일
            1000  // 시작 가격
    );

    public static ProductSaveRequest 상품_등록_요청_아이디_누락_유효성_검증실패_요청 = createProductSaveRequest(
            "",  // 사용자 아이디 비워둠
            상품_이름,
            상품_설명,
            현재시간.plusDays(1),
            현재시간.plusDays(2),
            1000
    );

    public static ProductSaveRequest 상품_등록_요청_상품설명_유효성_검증실패_요청 = createProductSaveRequest(
            UserFixtures.유저_아이디,
            상품_이름,
            "",  // 상품 설명 비워둠
            현재시간.plusDays(1),
            현재시간.plusDays(2),
            1000
    );

    public static ProductSaveRequest 상품_등록_요청_시작일_유효성_검증실패_요청 = createProductSaveRequest(
            UserFixtures.유저_아이디,
            상품_이름,
            상품_설명,
            현재시간.minusDays(1),  // 현재 시각 이전
            현재시간.plusDays(2),
            1000
    );

    public static ProductSaveRequest 상품_등록_요청_종료일_유효성_검증실패_요청 = createProductSaveRequest(
            UserFixtures.유저_아이디,
            상품_이름,
            상품_설명,
            현재시간.plusDays(1),
            현재시간.plusDays(1),  // 시작일과 동일
            1000
    );

    public static ProductSaveRequest 상품_등록_요청_시작가격_유효성_검증실패_요청 = createProductSaveRequest(
            UserFixtures.유저_아이디,
            상품_이름,
            상품_설명,
            현재시간.plusDays(1),
            현재시간.plusDays(2),
            500  // 시작 가격이 1000 미만
    );

    public static ProductUpdateRequest 상품_수정_요청_상품명_유효성_검증실패_요청 = createProductUpdateRequest(
            "",  // 상품명 비워둠
            상품_설명,
            현재시간.plusDays(1),  // 경매 시작일
            현재시간.plusDays(2),  // 경매 종료일
            1000  // 시작 가격
    );

    public static ProductUpdateRequest 상품_수정_요청_상품설명_유효성_검증실패_요청 = createProductUpdateRequest(
            상품_이름,
            "",  // 상품 설명 비워둠
            현재시간.plusDays(1),
            현재시간.plusDays(2),
            1000
    );

    public static ProductUpdateRequest 상품_수정_요청_시작일_유효성_검증실패_요청 = createProductUpdateRequest(
            상품_이름,
            상품_설명,
            현재시간.minusDays(1),  // 현재 시각 이전
            현재시간.plusDays(2),
            1000
    );

    public static ProductUpdateRequest 상품_수정_요청_종료일_유효성_검증실패_요청 = createProductUpdateRequest(
            상품_이름,
            상품_설명,
            현재시간.plusDays(1),
            현재시간.plusDays(1),  // 시작일과 동일
            1000
    );

    public static ProductUpdateRequest 상품_수정_요청_시작가격_유효성_검증실패_요청 = createProductUpdateRequest(
            상품_이름,
            상품_설명,
            현재시간.plusDays(1),
            현재시간.plusDays(2),
            500  // 시작 가격이 1000 미만
    );

    public static ProductSaveRequest 상품_등록_요청 = createProductSaveRequest(UserFixtures.유저_아이디, 상품_이름, 상품_설명, 현재시간, 종료일,
            시작가격);
    public static ProductUpdateRequest 상품_수정_요청 = createProductUpdateRequest(상품_이름, 상품_설명, 현재시간, 종료일, 시작가격);
    public static ProductResponse 상품_응답 = ProductResponse.from(1L);

    public static ProductFindResponse 상품_조회_응답 = ProductFindResponse.from(상품, ImagesFixtures.이미지_응답);
    public static ProductFindResponse 상품_조회_응답2 = ProductFindResponse.from(상품, ImagesFixtures.이미지_응답);
    public static List<ProductFindResponse> 상품_조회_응담_여러개 = List.of(상품_조회_응답, 상품_조회_응답2);
    public static PagingResponse<ProductFindResponse> 페이징_상품_조회 = PagingResponse.from(상품_조회_응담_여러개, 0);
    public static NotFoundProductException 존재하지_않는_상품_예외 = new NotFoundProductException(999L);
    public static ProductLikeResponse 상품_좋아요_응답 = new ProductLikeResponse(10);
    public static ProductNotPendingException 경매_상품_상태_접근_불가능 = new ProductNotPendingException(999L);
    public static UnauthorizedProductAccessException 경매_상품_접근_권한_없음 = new UnauthorizedProductAccessException(
            "UnAuthorizedUser", 999L);

    public static IllegalArgumentException 상품명_유효성_검증실패_예외메시지 = new IllegalArgumentException("상품명은 필수 값입니다.");
    public static IllegalArgumentException 사용자아이디_누락_유효성_검증실패_예외메시지 = new IllegalArgumentException("등록자 아이디는 필수 값입니다.");
    public static IllegalArgumentException 상품설명_유효성_검증실패_예외메시지 = new IllegalArgumentException("상품 설명은 필수 값입니다.");
    public static IllegalArgumentException 시작일_유효성_검증실패_예외메시지 = new IllegalArgumentException(
            "경매 시작일이 현재 시각보다 이전일 수 없습니다.");
    public static IllegalArgumentException 종료일_유효성_검증실패_예외메시지 = new IllegalArgumentException(
            "경매 종료일이 경매 시작일보다 이전일 수 없습니다.");
    public static IllegalArgumentException 시작가격_유효성_검증실패_예외메시지 = new IllegalArgumentException("시작 가격은 최소 1000원입니다.");
    public static IllegalArgumentException 이미지_유효성_검증실패_예외메시지 = new IllegalArgumentException("경매 상품에 대한 이미지는 필수 값입니다.");

}
