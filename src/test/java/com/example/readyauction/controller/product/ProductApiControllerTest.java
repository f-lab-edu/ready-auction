package com.example.readyauction.controller.product;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;

import com.example.readyauction.controller.request.product.ProductSaveRequest;
import com.example.readyauction.controller.request.product.ProductUpdateRequest;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.fixture.ImagesFixtures;
import com.example.readyauction.fixture.ProductFixtures;
import com.example.readyauction.fixture.WithMockCustomUser;
import com.example.readyauction.service.product.ProductFacade;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class ProductApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductFacade productFacade;

    @Test
    @DisplayName("상품 등록 API")
    @WithMockCustomUser
    public void 상품_등록_성공() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_등록_요청).getBytes(StandardCharsets.UTF_8)
        );

        when(
            productFacade.enroll(
                any(User.class),
                any(ProductSaveRequest.class),
                argThat(list -> list.size() == 2))) // 예시로 이미지 2개로 가정
            .thenReturn(ProductFixtures.상품_응답);

        mockMvc.perform(multipart("/api/v1/products")
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("상품 등록 성공",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                // Product JSON 구조는 따로 설명하는 주석을 추가
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("상품 등록 ID")
                )
            ));
    }

    @Test
    @DisplayName("상품 등록 API - 이미지 누락")
    @WithMockCustomUser
    public void 상품_등록_이미지누락_검증실패() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_등록_요청).getBytes(StandardCharsets.UTF_8)
        );

        when(
            productFacade.enroll(
                any(User.class),
                any(ProductSaveRequest.class),
                anyList())) // 예시로 이미지 2개로 가정
            .thenThrow(ProductFixtures.이미지_유효성_검증실패_예외메시지);

        mockMvc.perform(multipart("/api/v1/products")
                .file(product)
                .file(ImagesFixtures.비어있는_이미지)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 등록 실패 - 이미지 누락",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                // Product JSON 구조는 따로 설명하는 주석을 추가
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("message").description("상품 등록 시 상품명 유효성 검증 실패 오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("상품 등록 API - 상품명 유효성 검증 실패")
    @WithMockCustomUser
    public void 상품_등록_상품명_유효성_검증실패() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_등록_요청_상품명_유효성_검증실패_요청).getBytes(StandardCharsets.UTF_8)
        );

        when(
            productFacade.enroll(
                any(User.class),
                any(ProductSaveRequest.class),
                argThat(list -> list.size() == 2))) // 예시로 이미지 2개로 가정
            .thenThrow(ProductFixtures.상품명_유효성_검증실패_예외메시지);

        mockMvc.perform(multipart("/api/v1/products")
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 등록 실패 - 상품명 누락",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("message").description("상품 등록 시 상품명 유효성 검증 실패 오류 메시지")
                )
            ));

    }

    @Test
    @DisplayName("상품 등록 API - 등록자 아이디 누락")
    @WithMockCustomUser
    public void 상품_등록_등록자_아이디_유효성_검증실패() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_등록_요청_아이디_누락_유효성_검증실패_요청)
                .getBytes(StandardCharsets.UTF_8)
        );

        when(
            productFacade.enroll(
                any(User.class),
                any(ProductSaveRequest.class),
                argThat(list -> list.size() == 2))) // 예시로 이미지 2개로 가정
            .thenThrow(ProductFixtures.사용자아이디_누락_유효성_검증실패_예외메시지);

        mockMvc.perform(multipart("/api/v1/products")
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 등록 실패 - 등록자 아이디 누락",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                // Product JSON 구조는 따로 설명하는 주석을 추가
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("message").description("상품 등록 시 상품명 유효성 검증 실패 오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("상품 등록 API - 상품 설명 누락")
    @WithMockCustomUser
    public void 상품_등록_상품설명_유효성_검증실패() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_등록_요청_상품설명_유효성_검증실패_요청).getBytes(StandardCharsets.UTF_8)
        );
        when(
            productFacade.enroll(
                any(User.class),
                any(ProductSaveRequest.class),
                argThat(list -> list.size() == 2))) // 예시로 이미지 2개로 가정
            .thenThrow(ProductFixtures.상품설명_유효성_검증실패_예외메시지);

        mockMvc.perform(multipart("/api/v1/products")
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 등록 실패 - 상품 설명 누락",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                // Product JSON 구조는 따로 설명하는 주석을 추가
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("message").description("상품 등록 시 상품설명 유효성 검증 실패 오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("상품 등록 API - 경매 시작일 문제")
    @WithMockCustomUser
    public void 상품_등록_시작일_유효성_검증실패() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_등록_요청_시작일_유효성_검증실패_요청).getBytes(StandardCharsets.UTF_8)
        );
        when(
            productFacade.enroll(
                any(User.class),
                any(ProductSaveRequest.class),
                argThat(list -> list.size() == 2))) // 예시로 이미지 2개로 가정
            .thenThrow(ProductFixtures.시작일_유효성_검증실패_예외메시지);

        mockMvc.perform(multipart("/api/v1/products")
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 등록 실패 - 시작일 문제",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                // Product JSON 구조는 따로 설명하는 주석을 추가
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("message").description("상품 등록 시 시작일 유효성 검증 실패 오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("상품 등록 API - 종료일 문제")
    @WithMockCustomUser
    public void 상품_등록_요청_종료일_유효성_검증실패() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_등록_요청_종료일_유효성_검증실패_요청).getBytes(StandardCharsets.UTF_8)
        );

        when(
            productFacade.enroll(
                any(User.class),
                any(ProductSaveRequest.class),
                argThat(list -> list.size() == 2))) // 예시로 이미지 2개로 가정
            .thenThrow(ProductFixtures.종료일_유효성_검증실패_예외메시지);

        mockMvc.perform(multipart("/api/v1/products")
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 등록 실패 - 종료일 문제",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                // Product JSON 구조는 따로 설명하는 주석을 추가
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("message").description("상품 등록 시 종료일 유효성 검증 실패 오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("상품 등록 API - 시작가격 문제")
    @WithMockCustomUser
    public void 상품_등록_요청_시작가격_유효성_검증실패() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_등록_요청_시작가격_유효성_검증실패_요청).getBytes(StandardCharsets.UTF_8)
        );

        when(
            productFacade.enroll(
                any(User.class),
                any(ProductSaveRequest.class),
                argThat(list -> list.size() == 2))) // 예시로 이미지 2개로 가정
            .thenThrow(ProductFixtures.시작가격_유효성_검증실패_예외메시지);

        mockMvc.perform(multipart("/api/v1/products")
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 등록 실패 - 시작가 문제",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("message").description("상품 등록 시 시작가 유효성 검증 실패 오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("단건 상품 조회")
    @WithMockCustomUser
    public void findById() throws Exception {
        when(
            productFacade.findById(1L))
            .thenReturn(ProductFixtures.상품_조회_응답);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/products/{id}", 1L))
            .andExpect(status().isOk())
            .andDo(document("상품 단건 조회",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                RequestDocumentation.pathParameters(
                    RequestDocumentation.parameterWithName("id").description("조회할 상품 ID")
                ),
                responseFields(
                    fieldWithPath("userId").type(JsonFieldType.STRING).description("상품 등록자 ID"),
                    fieldWithPath("productName").type(JsonFieldType.STRING).description("상품 이름"),
                    fieldWithPath("description").type(JsonFieldType.STRING).description("상품 설명"),
                    fieldWithPath("startDate").type(JsonFieldType.STRING).description("시작일"),
                    fieldWithPath("closeDate").type(JsonFieldType.STRING).description("종료일"),
                    fieldWithPath("startPrice").type(JsonFieldType.NUMBER).description("시작 가격"),
                    fieldWithPath("imageResponses[]")
                        .type(JsonFieldType.ARRAY)
                        .description("상품 이미지 리스트"),
                    fieldWithPath("imageResponses[].originalName")
                        .type(JsonFieldType.STRING)
                        .description("이미지 원본 이름"),
                    fieldWithPath("imageResponses[].imagePath")
                        .type(JsonFieldType.STRING)
                        .description("이미지 경로"))
            ));
    }

    @Test
    @DisplayName("단건 상품 조회 API - 실패")
    @WithMockCustomUser
    public void findByIdFail() throws Exception {
        when(
            productFacade.findById(999L))
            .thenThrow(ProductFixtures.존재하지_않는_상품_예외);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/products/{id}", 999L))
            .andExpect(status().isNotFound()) // 존재하지 않는 경우 404
            .andDo(document("상품 단건 조회 실패 - 존재하지 않는 상품",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                RequestDocumentation.pathParameters(
                    RequestDocumentation.parameterWithName("id").description("조회한 상품 ID")
                ),
                responseFields(
                    fieldWithPath("message").description("상품 조회 실패한 상품 ID와 오류 메시지")
                ))
            );
    }

    @Test
    @DisplayName("여러 상품 조회 API")
    @WithMockCustomUser
    public void findAll() throws Exception {
        // 작성 예정
        when(productFacade.findAll(
            anyString(),
            any(),
            anyInt(),
            anyInt(),
            any()))
            .thenReturn(ProductFixtures.페이징_상품_조회);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/products")
                .param("keyword", "test") // 검색 키워드 추가
                .param("productCondition", "ACTIVE") // 상품 상태 추가
                .param("pageNo", "0") // 현재 페이지 번호 추가
                .param("pageSize", "10") // 페이지당 상품 수 추가
                .param("orderBy", "START_DATE")) // 정렬 기준 추가
            .andExpect(status().isOk())
            .andDo(document("상품 리스트 조회",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                RequestDocumentation.queryParameters(
                    RequestDocumentation.parameterWithName("keyword").description("검색 키워드"),
                    RequestDocumentation.parameterWithName("productCondition").description("경매 상품 상태"),
                    RequestDocumentation.parameterWithName("pageNo").description("페이지 수"),
                    RequestDocumentation.parameterWithName("pageSize").description("페이지당 존재하는 상품 수"),
                    RequestDocumentation.parameterWithName("orderBy").description("정렬 기준")
                ),
                responseFields(
                    fieldWithPath("items[]").description("상품 리스트"),
                    fieldWithPath("items[].userId").description("상품 등록자 ID"),
                    fieldWithPath("items[].imageResponses[]").description("상품 이미지"),
                    fieldWithPath("items[].imageResponses[].originalName")
                        .description("원본 이미지 이름"),
                    fieldWithPath("items[].imageResponses[].imagePath").description("이미지 경로"),
                    fieldWithPath("items[].productName").description("상품 이름"),
                    fieldWithPath("items[].description").description("상품 설명"),
                    fieldWithPath("items[].startDate").description("시작일"),
                    fieldWithPath("items[].closeDate").description("종료일"),
                    fieldWithPath("items[].startPrice").description("시작 가격"),
                    fieldWithPath("pageNo").description("현재 페이지 번호")
                )
            ));

    }

    @Test
    @DisplayName("상품 수정 API")
    @WithMockCustomUser
    public void 상품_수정_성공() throws Exception {
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_수정_요청).getBytes(StandardCharsets.UTF_8)
        );

        when(
            productFacade.update(
                any(User.class),
                eq(1L),
                any(ProductUpdateRequest.class),
                anyList()))
            .thenReturn(ProductFixtures.상품_응답);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/products/{id}", 1L)
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("상품 수정 성공",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                // Product JSON 구조는 따로 설명하는 주석을 추가
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("수정한 상품 ID")
                )
            ));
    }

    @Test
    @DisplayName("상품 수정 API - 실패(상품 상태 NOT PENDING)")
    @WithMockCustomUser
    public void 상품_경매진행중_수정_실패() throws Exception {
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_수정_요청).getBytes(StandardCharsets.UTF_8)
        );

        when(
            productFacade.update(
                any(User.class),
                eq(999L),
                any(ProductUpdateRequest.class),
                anyList()))
            .thenThrow(ProductFixtures.경매_상품_상태_접근_불가능);
        // Multipart/form-data 요청은 default가 POST, 그래서 PUT을 따로 지정해줘야함.
        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/products/{id}", 999L)
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 수정 실패 - 경매 진행중",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("message").description("오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("상품 수정 API - 상품명 유효성 검증 실패")
    @WithMockCustomUser
    public void 상품_수정_상품명_유효성_검증실패() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_수정_요청_상품명_유효성_검증실패_요청).getBytes(StandardCharsets.UTF_8)
        );

        when(
            productFacade.update(
                any(User.class),
                eq(1L),
                any(ProductUpdateRequest.class),
                anyList()))
            .thenThrow(ProductFixtures.상품명_유효성_검증실패_예외메시지);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/products/{id}", 1L)
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 수정 실패 - 상품명 누락",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("message").description("상품 등록 시 상품명 유효성 검증 실패 오류 메시지")
                )
            ));

    }

    @Test
    @DisplayName("상품 수정 API - 상품 설명 누락")
    @WithMockCustomUser
    public void 상품_수정_상품설명_유효성_검증실패() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_수정_요청_상품설명_유효성_검증실패_요청).getBytes(StandardCharsets.UTF_8)
        );
        when(
            productFacade.update(
                any(User.class),
                eq(1L),
                any(ProductUpdateRequest.class),
                anyList()))
            .thenThrow(ProductFixtures.상품설명_유효성_검증실패_예외메시지);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/products/{id}", 1L)
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 수정 실패 - 상품 설명 누락",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                // Product JSON 구조는 따로 설명하는 주석을 추가
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("message").description("상품 등록 시 상품설명 유효성 검증 실패 오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("상품 수정 API - 경매 시작일 문제")
    @WithMockCustomUser
    public void 상품_수정_시작일_유효성_검증실패() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_수정_요청_시작일_유효성_검증실패_요청).getBytes(StandardCharsets.UTF_8)
        );
        when(
            productFacade.update(
                any(User.class),
                eq(1L),
                any(ProductUpdateRequest.class),
                anyList()))
            .thenThrow(ProductFixtures.시작일_유효성_검증실패_예외메시지);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/products/{id}", 1L)
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 수정 실패 - 시작일 문제",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                // Product JSON 구조는 따로 설명하는 주석을 추가
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("message").description("상품 등록 시 시작일 유효성 검증 실패 오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("상품 수정 API - 종료일 문제")
    @WithMockCustomUser
    public void 상품_수정_요청_종료일_유효성_검증실패() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_수정_요청_종료일_유효성_검증실패_요청).getBytes(StandardCharsets.UTF_8)
        );

        when(
            productFacade.update(
                any(User.class),
                eq(1L),
                any(ProductUpdateRequest.class),
                anyList()))
            .thenThrow(ProductFixtures.종료일_유효성_검증실패_예외메시지);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/products/{id}", 1L)
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 수정 실패 - 종료일 문제",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                // Product JSON 구조는 따로 설명하는 주석을 추가
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("message").description("상품 등록 시 종료일 유효성 검증 실패 오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("상품 수정 API - 시작가격 문제")
    @WithMockCustomUser
    public void 상품_수정_요청_시작가격_유효성_검증실패() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_수정_요청_시작가격_유효성_검증실패_요청).getBytes(StandardCharsets.UTF_8)
        );

        when(
            productFacade.update(
                any(User.class),
                eq(1L),
                any(ProductUpdateRequest.class),
                anyList()))
            .thenThrow(ProductFixtures.시작가격_유효성_검증실패_예외메시지);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/products/{id}", 1L)
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 수정 실패 - 시작가 문제",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("message").description("상품 등록 시 시작가 유효성 검증 실패 오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("상품 수정 API - 경매 진행중 문제")
    @WithMockCustomUser
    public void 상품_수정_요청_경매진행중_실패() throws Exception {
        // @RequestPart
        MockMultipartFile product = new MockMultipartFile(
            "product",
            "",
            "application/json",
            objectMapper.writeValueAsString(ProductFixtures.상품_수정_요청_시작가격_유효성_검증실패_요청).getBytes(StandardCharsets.UTF_8)
        );

        when(
            productFacade.update(
                any(User.class),
                eq(1L),
                any(ProductUpdateRequest.class),
                anyList()))
            .thenThrow(ProductFixtures.경매_상품_상태_접근_불가능);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/products/{id}", 1L)
                .file(product)
                .file(ImagesFixtures.mockMultipartFiles.get(0))
                .file(ImagesFixtures.mockMultipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("상품 수정 실패 - 경매 진행중",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestParts(
                    partWithName("product").description("상품 정보 JSON"),
                    partWithName("images").description("상품 이미지 파일들") // 이미지 파일에 대한 설명
                ),
                responseFields(
                    fieldWithPath("message").description("경매 진행중이어서 수정 불가능 오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("상품 삭제 API")
    @WithMockCustomUser
    public void 상품_삭제() throws Exception {

        when(productFacade.delete(any(User.class), eq(1L)))
            .thenReturn(ProductFixtures.상품_응답);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/products/{id}", 1L))
            .andExpect(status().isOk())
            .andDo(document("상품 삭제 성공",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                RequestDocumentation.pathParameters(
                    RequestDocumentation.parameterWithName("id").description("삭제할 상품 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("상품 삭제된 ID")
                )
            ));
    }

    @Test
    @DisplayName("상품 삭제 API - 실패") // 본인 등록 상품만 삭제 가능
    @WithMockCustomUser
    public void 상품_삭제_실패() throws Exception {
        when(productFacade.delete(any(User.class), eq(999L)))
            .thenThrow(ProductFixtures.경매_상품_접근_권한_없음);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/products/{id}", 999L))
            .andExpect(status().isUnauthorized()) // 401로 하는게 맞나? 흠...
            .andDo(document("상품 삭제 실패 - 본인 등록 상품이 아닌 경우", // 근데 이런 경우가 없을꺼같긴하지만..
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                RequestDocumentation.pathParameters(
                    RequestDocumentation.parameterWithName("id").description("삭제할 상품 ID")
                ),
                responseFields(
                    fieldWithPath("message").description("오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("상품 삭제 API - 실패(상품 상태 NOT PENDING)")
    @WithMockCustomUser
    public void 상품_경매진행중_삭제_실패() throws Exception {
        when(productFacade.delete(any(User.class), eq(999L)))
            .thenThrow(ProductFixtures.경매_상품_접근_권한_없음);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/products/{id}", 999L))
            .andExpect(status().isUnauthorized()) // 401로 하는게 맞나? 흠...
            .andDo(document("상품 삭제 실패 - 경매 진행중",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                RequestDocumentation.pathParameters(
                    RequestDocumentation.parameterWithName("id").description("삭제할 상품 ID")
                ),
                responseFields(
                    fieldWithPath("message").description("오류 메시지")
                )
            ));
    }

    @Test
    @DisplayName("상품 좋아요 추가 API")
    @WithMockCustomUser
    public void 상품_좋아요() throws Exception {

        when(productFacade.addLike(any(User.class), any(Long.class)))
            .thenReturn(ProductFixtures.상품_좋아요_응답);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/products/{id}/likes", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("상품 좋아요",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                RequestDocumentation.pathParameters(
                    RequestDocumentation.parameterWithName("id").description("좋아요누른 상품 ID")
                ),
                responseFields(
                    fieldWithPath("likesCount").description("해당 기능 동작 후 좋아요 개수")
                )
            ));

    }

    @Test
    @DisplayName("상품 좋아요 삭제 API 테스트")
    @WithMockCustomUser
    public void 상품_좋아요_삭제() throws Exception {

        when(productFacade.productLikeDelete(any(User.class), any(Long.class)))
            .thenReturn(ProductFixtures.상품_좋아요_응답);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/products/{id}/likes", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("상품 좋아요 삭제",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                RequestDocumentation.pathParameters(
                    RequestDocumentation.parameterWithName("id").description("좋아요 취소 누른 상품 ID")
                ),
                responseFields(
                    fieldWithPath("likesCount").description("해당 기능 동작 후 좋아요 개수")
                )
            ));
    }

    @Test
    @DisplayName("상품 좋아요 수 조회 API 테스트")
    @WithMockCustomUser
    public void testGetProductLike() throws Exception {

        when(productFacade.getProductLikes(any(Long.class)))
            .thenReturn(ProductFixtures.상품_좋아요_응답);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/products/{id}/likes", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("상품 좋아요 삭제",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                RequestDocumentation.pathParameters(
                    RequestDocumentation.parameterWithName("id").description("좋아요 취소 누른 상품 ID")
                ),
                responseFields(
                    fieldWithPath("likesCount").description("해당 기능 동작 후 좋아요 개수")
                )
            ));
    }

}
