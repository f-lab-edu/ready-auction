package com.example.readyauction.controller.auction;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.readyauction.controller.request.auction.BidRequest;
import com.example.readyauction.domain.user.CustomUserDetails;
import com.example.readyauction.fixture.AuctionFixtures;
import com.example.readyauction.fixture.WithMockCustomUser;
import com.example.readyauction.service.auction.AuctionService;
import com.example.readyauction.service.auction.HighestBidSseNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuctionService auctionService;

    @MockBean
    private HighestBidSseNotificationService bidSseNotificationService;

    // 가격 입찰 성공
    @Test
    @DisplayName("가격 입찰 API")
    @WithMockCustomUser
    public void tender() throws Exception {
        when(auctionService.biddingPrice(any(CustomUserDetails.class), any(BidRequest.class),
            eq(1L)))
            .thenReturn(AuctionFixtures.가격_제안_응답);

        mockMvc.perform(post("/api/v1/auctions/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AuctionFixtures.가격_제안_요청)))
            .andExpect(status().isOk())
            .andDo(document("경매 가격 제안",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                ),
                requestFields(
                    fieldWithPath("biddingPrice").type(JsonFieldType.NUMBER).description("입찰자가 제안한 가격")
                ),
                responseFields(
                    fieldWithPath("productId").description("입찰 성공한 상품 ID"),
                    fieldWithPath("rateOfIncrease").description("이전 최고가 대비 상승률")
                ))
            );

    }

    @Test
    @DisplayName("가격 입찰 API - 실채")
    @WithMockCustomUser
    public void tenderFail() throws Exception {
        when(auctionService.biddingPrice(any(CustomUserDetails.class), any(BidRequest.class),
            eq(1L)))
            .thenThrow(AuctionFixtures.가격_제안_실패_예외);

        mockMvc.perform(post("/api/v1/auctions/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AuctionFixtures.가격_제안_요청)))
            .andExpect(status().isBadRequest())
            .andDo(document("경매 가격 제안 입찰 실패",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                ),
                requestFields(
                    fieldWithPath("biddingPrice").type(JsonFieldType.NUMBER
                    ).description("입찰자가 제안한 가격")
                ),
                responseFields(
                    fieldWithPath("message").description("입찰 실패 오류 메시지")
                ))
            );

    }

    @Test
    @DisplayName("가격 입찰 API - 분산락 획득 실패")
    @WithMockCustomUser
    public void tenderFailCausedByRedisLockAcquired() throws Exception {
        when(auctionService.biddingPrice(any(CustomUserDetails.class), any(BidRequest.class),
            eq(1L)))
            .thenThrow(AuctionFixtures.레디스_분산락_획득실패_예외);

        mockMvc.perform(post("/api/v1/auctions/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AuctionFixtures.가격_제안_요청)))
            .andExpect(status().is5xxServerError())
            .andDo(document("경매 가격 제안 실패 - 분산락 획득",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                ),
                requestFields(
                    fieldWithPath("biddingPrice").type(JsonFieldType.NUMBER).description("입찰자가 제안한 가격")
                ),
                responseFields(
                    fieldWithPath("message").description("레디스 분산락 획득 실패 오류 메시지")
                ))
            );
    }

    @Test
    @DisplayName("가격 입찰 API - 분산락 처리과정 실패")
    @WithMockCustomUser
    public void tenderFailCausedByRedisLockInterrupted() throws Exception {
        when(auctionService.biddingPrice(any(CustomUserDetails.class), any(BidRequest.class),
            eq(1L)))
            .thenThrow(AuctionFixtures.레디스_분산락_처리과정_예외);

        mockMvc.perform(post("/api/v1/auctions/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AuctionFixtures.가격_제안_요청)))
            .andExpect(status().is5xxServerError())
            .andDo(document("경매 가격 제안 실패 - 분산락 처리과정",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                ),
                requestFields(
                    fieldWithPath("biddingPrice").type(JsonFieldType.NUMBER).description("입찰자가 제안한 가격")
                ),
                responseFields(
                    fieldWithPath("message").description("레디스 분산락 처리과정 실패 오류 메시지")
                ))
            );
    }

    // 경매 구독 API - SSE 어떻게할지 고민..
}
