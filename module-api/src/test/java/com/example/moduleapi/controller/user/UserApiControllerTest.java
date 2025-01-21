package com.example.moduleapi.controller.user;

import com.example.moduleapi.config.WebSecurityConfig;
import com.example.moduleapi.controller.request.user.LoginRequest;
import com.example.moduleapi.controller.request.user.UserSaveRequest;
import com.example.moduleapi.fixture.UserFixtures;
import com.example.moduleapi.fixture.WithMockCustomUser;
import com.example.moduleapi.service.user.LoginService;
import com.example.moduleapi.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserApiController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import(WebSecurityConfig.class)
class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private LoginService loginService;

    @Test
    @DisplayName("회원가입 API")
    public void join() throws Exception {
        when(userService.join(
                any(UserSaveRequest.class))).thenReturn(UserFixtures.유저_응답);

        mockMvc.perform(post("/api/v1/users")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(UserFixtures.유저_아이디_회원가입_요청)))
               .andExpect(status().isOk())
               .andDo(document("회원가입",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),
                       requestFields(
                               fieldWithPath("userId").type(JsonFieldType.STRING)
                                                      .description("아이디"),
                               fieldWithPath("password").type(JsonFieldType.STRING)
                                                        .description("비밀번호"),
                               fieldWithPath("name").type(JsonFieldType.STRING)
                                                    .description("이름"),
                               fieldWithPath("birthDate").type(JsonFieldType.STRING)
                                                         .description("생년월일 (형식: YYYY-MM-DD)"),
                               fieldWithPath("gender").type(JsonFieldType.STRING)
                                                      .description("성별")
                       ),
                       responseFields(
                               fieldWithPath("userId").type(JsonFieldType.STRING)
                                                      .description("아이디"),
                               fieldWithPath("name").type(JsonFieldType.STRING)
                                                    .description("이름")
                       )
               ));
    }

    @Test
    @DisplayName("회원가입 API - 이름 누락 실패")
    public void validateUserNameFail() throws Exception {
        when(userService.join(
                any(UserSaveRequest.class))).thenThrow(UserFixtures.유저_이름_유효성_검증실패_예외메시지);

        mockMvc.perform(post("/api/v1/users")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(UserFixtures.유저_회원가입_요청_이름_유효성_검증실패_요청)))
               .andExpect(status().isBadRequest())
               .andDo(document("회원가입 실패-이름 누락",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),
                       requestFields(
                               fieldWithPath("userId").type(JsonFieldType.STRING)
                                                      .description("아이디"),
                               fieldWithPath("password").type(JsonFieldType.STRING)
                                                        .description("비밀번호"),
                               fieldWithPath("name").type(JsonFieldType.STRING)
                                                    .description("이름"),
                               fieldWithPath("birthDate").type(JsonFieldType.STRING)
                                                         .description("생년월일 (형식: YYYY-MM-DD)"),
                               fieldWithPath("gender").type(JsonFieldType.STRING)
                                                      .description("성별")
                       ),
                       responseFields(
                               fieldWithPath("message").description("오류 메시지")
                       ))
               );
    }

    @Test
    @DisplayName("회원가입 API - 유저 아이디 누락 실패")
    public void validateUserIdFail() throws Exception {
        when(userService.join(
                any(UserSaveRequest.class))).thenThrow(UserFixtures.유저_아이디_누락_유효성_검증실패_예외메시지);

        mockMvc.perform(post("/api/v1/users")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(UserFixtures.유저_회원가입_요청_아이디_누락_유효성_검증실패_요청)))
               .andExpect(status().isBadRequest())
               .andDo(document("회원가입 실패 - 아이디 누락",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),
                       requestFields(
                               fieldWithPath("userId").type(JsonFieldType.STRING)
                                                      .description("아이디"),
                               fieldWithPath("password").type(JsonFieldType.STRING)
                                                        .description("비밀번호"),
                               fieldWithPath("name").type(JsonFieldType.STRING)
                                                    .description("이름"),
                               fieldWithPath("birthDate").type(JsonFieldType.STRING)
                                                         .description("생년월일 (형식: YYYY-MM-DD)"),
                               fieldWithPath("gender").type(JsonFieldType.STRING)
                                                      .description("성별")
                       ),
                       responseFields(
                               fieldWithPath("message").description("오류 메시지")
                       ))
               );
    }

    @Test
    @DisplayName("회원가입 API - 유저 아이디 길이 실패")
    public void validateUserIdLenFail() throws Exception {
        when(userService.join(any(UserSaveRequest.class)))
                .thenThrow(UserFixtures.유저_아이디_길이_유효성_검증실패_예외메시지);

        mockMvc.perform(post("/api/v1/users")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(UserFixtures.유저_회원가입_요청_아이디_길이_유효성_검증실패_요청)))
               .andExpect(status().isBadRequest())
               .andDo(document("회원가입 실패 - 아이디 길이",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),
                       requestFields(
                               fieldWithPath("userId").type(JsonFieldType.STRING)
                                                      .description("아이디"),
                               fieldWithPath("password").type(JsonFieldType.STRING)
                                                        .description("비밀번호"),
                               fieldWithPath("name").type(JsonFieldType.STRING)
                                                    .description("이름"),
                               fieldWithPath("birthDate").type(JsonFieldType.STRING)
                                                         .description("생년월일 (형식: YYYY-MM-DD)"),
                               fieldWithPath("gender").type(JsonFieldType.STRING)
                                                      .description("성별")
                       ),
                       responseFields(
                               fieldWithPath("message").description("오류 메시지")
                       ))
               );
    }

    @Test
    @DisplayName("회원가입 API - 비밀번호 누락 실패")
    public void validateUserPasswordFail() throws Exception {
        when(userService.join(any(UserSaveRequest.class)))
                .thenThrow(UserFixtures.유저_비밀번호_누락_검증실패_예외메시지);

        mockMvc.perform(post("/api/v1/users")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(UserFixtures.유저_회원가입_요청_비밀번호_누락_유효성_검증실패_요청)))
               .andExpect(status().isBadRequest())
               .andDo(document("회원가입 실패 - 비밀번호 누락",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),
                       requestFields(
                               fieldWithPath("userId").type(JsonFieldType.STRING)
                                                      .description("아이디"),
                               fieldWithPath("password").type(JsonFieldType.STRING)
                                                        .description("비밀번호"),
                               fieldWithPath("name").type(JsonFieldType.STRING)
                                                    .description("이름"),
                               fieldWithPath("birthDate").type(JsonFieldType.STRING)
                                                         .description("생년월일 (형식: YYYY-MM-DD)"),
                               fieldWithPath("gender").type(JsonFieldType.STRING)
                                                      .description("성별")
                       ),
                       responseFields(
                               fieldWithPath("message").description("오류 메시지")
                       ))
               );
    }

    @Test
    @DisplayName("회원가입 API - 비밀번호 길이 실패")
    public void validateUserPasswordLenFail() throws Exception {
        when(userService.join(
                any(UserSaveRequest.class))).thenThrow(UserFixtures.유저_비밀번호_길이_유효성_검증실패_예외메시지);

        mockMvc.perform(post("/api/v1/users")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(UserFixtures.유저_회원가입_요청_비밀번호_길이_유효성_검증실패_요청)))
               .andExpect(status().isBadRequest())
               .andDo(document("회원가입 실패-비밀번호 길이",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),
                       requestFields(
                               fieldWithPath("userId").type(JsonFieldType.STRING)
                                                      .description("아이디"),
                               fieldWithPath("password").type(JsonFieldType.STRING)
                                                        .description("비밀번호"),
                               fieldWithPath("name").type(JsonFieldType.STRING)
                                                    .description("이름"),
                               fieldWithPath("birthDate").type(JsonFieldType.STRING)
                                                         .description("생년월일 (형식: YYYY-MM-DD)"),
                               fieldWithPath("gender").type(JsonFieldType.STRING)
                                                      .description("성별")
                       ),
                       responseFields(
                               fieldWithPath("message").description("오류 메시지")
                       ))
               );
    }

    @Test
    @DisplayName("회원가입 API - 비빌번호 정규식 유효성 검증 실패")
    public void validateUserPasswordRegexFail() throws Exception {

        when(userService.join(
                any(UserSaveRequest.class))).thenThrow(UserFixtures.유저_비밀번호_정규식_유효성_검증실패_예외메시지);

        mockMvc.perform(post("/api/v1/users")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(UserFixtures.유저_회원가입_요청_비밀번호_정규식_검증실패_요청)))
               .andExpect(status().isBadRequest())
               .andDo(document("회원가입 실패 - 비밀번호 정규식 불일치",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),
                       requestFields(
                               fieldWithPath("userId").type(JsonFieldType.STRING)
                                                      .description("아이디"),
                               fieldWithPath("password").type(JsonFieldType.STRING)
                                                        .description("비밀번호"),
                               fieldWithPath("name").type(JsonFieldType.STRING)
                                                    .description("이름"),
                               fieldWithPath("birthDate").type(JsonFieldType.STRING)
                                                         .description("생년월일 (형식: YYYY-MM-DD)"),
                               fieldWithPath("gender").type(JsonFieldType.STRING)
                                                      .description("성별")
                       ),
                       responseFields(
                               fieldWithPath("message").description("오류 메시지")
                       ))
               );
    }

    @Test
    @DisplayName("회원가입 API - 중복 유저 아이디로 인한 실패")
    public void duplicateUserId() throws Exception {
        when(userService.join(
                any(UserSaveRequest.class))).thenThrow(UserFixtures.유저_아이디_중복_예외);

        mockMvc.perform(post("/api/v1/users")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(UserFixtures.유저_아이디_회원가입_요청)))
               .andExpect(status().isConflict())
               .andDo(document("회원가입 실패 - 중복 유저 아이디",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),
                       requestFields(
                               fieldWithPath("userId").type(JsonFieldType.STRING)
                                                      .description("아이디"),
                               fieldWithPath("password").type(JsonFieldType.STRING)
                                                        .description("비밀번호"),
                               fieldWithPath("name").type(JsonFieldType.STRING)
                                                    .description("이름"),
                               fieldWithPath("birthDate").type(JsonFieldType.STRING)
                                                         .description("생년월일 (형식: YYYY-MM-DD)"),
                               fieldWithPath("gender").type(JsonFieldType.STRING)
                                                      .description("성별")
                       ),
                       responseFields(
                               fieldWithPath("message").description("오류 메시지")
                       ))
               );
    }

    @Test
    @DisplayName("로그인 API")
    public void login() throws Exception {
        when(loginService.login(any(LoginRequest.class))).thenReturn(UserFixtures.유저_아이디);

        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        when(loginService.loadUserByUsername(UserFixtures.유저_아이디)).thenReturn(mockUserDetails);

        mockMvc.perform(post("/api/v1/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(UserFixtures.로그인_요청)))
               .andExpect(status().isOk())
               .andDo(document("로그인",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),
                       requestFields(
                               fieldWithPath("userId").type(JsonFieldType.STRING)
                                                      .description("아이디"),
                               fieldWithPath("password").type(JsonFieldType.STRING)
                                                        .description("비밀번호")
                       )
               ));
    }

    @Test
    @DisplayName("로그아웃 API")
    @WithMockCustomUser
    public void logout() throws Exception {
        mockMvc.perform(post("/api/v1/users/logout"))
               .andExpect(status().is3xxRedirection())
               .andDo(document("로그아웃",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint())
               ));
    }

}
