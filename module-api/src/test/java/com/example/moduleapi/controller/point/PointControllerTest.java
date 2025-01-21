package com.example.moduleapi.controller.point;

import com.example.moduleapi.config.WebSecurityConfig;
import com.example.moduleapi.controller.request.point.PointAmount;
import com.example.moduleapi.fixture.PointFixtures;
import com.example.moduleapi.fixture.WithMockCustomUser;
import com.example.moduleapi.service.point.PointService;
import com.example.moduledomain.domain.user.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PointController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import(WebSecurityConfig.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PointService pointService;

    @Test
    @DisplayName("포인트 충전")
    @WithMockCustomUser
    public void pointChargeTest() throws Exception {
        int chargedAmount = 100000;
        int currentBalance = 150000;

        when(pointService.chargePoint(
                any(CustomUserDetails.class),
                any(PointAmount.class))).thenReturn(PointFixtures.포인트_응답(chargedAmount, currentBalance));

        mockMvc.perform(post("/api/v1/point/charge")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(PointFixtures.포인트_충전)))
               .andExpect(status().isOk())
               .andDo(document("포인트 충전",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),
                       requestFields(
                               fieldWithPath("amount").type(JsonFieldType.NUMBER)
                                                      .description("충전할 포인트")
                       ),
                       responseFields(
                               fieldWithPath("currentPoint").description("현재 포인트 잔액"),
                               fieldWithPath("message").description("충전된 포인트 금액과 현재 잔액을 포함한 메시지. 예시: '포인트 100000원 충전 완료. [현재 포인트 잔액 : 200000원]'")
                       ))
               );
    }

}
