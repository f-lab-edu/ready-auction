package com.example.moduleapi.fixture;

import com.example.moduleapi.controller.request.user.LoginRequest;
import com.example.moduleapi.controller.request.user.UserSaveRequest;
import com.example.moduleapi.controller.response.user.UserResponse;
import com.example.moduleapi.exception.user.DuplicatedUserIdException;
import com.example.moduledomain.domain.user.Gender;
import com.example.moduledomain.domain.user.User;

import java.time.LocalDate;

public class UserFixtures {
    public static String 유저_아이디 = "test0512";
    public static String 유저_아이디_길이_문제 = "test";
    public static String 암호화_비빌번호 = "TestPwd123!";
    public static String 비빌번호_길이_문제 = "Pwd1!";
    public static String 비빌번호_정규식_문제 = "TestPwd123";
    public static String 이름 = "테스트";

    public static LoginRequest 로그인_요청 = LoginRequest.builder()
                                                    .userId(유저_아이디)
                                                    .password("TestPwdd123!")
                                                    .build();
    public static User 유저 = User.builder()
                                .userId(유저_아이디)
                                .name(이름)
                                .encodedPassword(암호화_비빌번호)
                                .build();

    public static UserSaveRequest 유저_아이디_회원가입_요청 = UserSaveRequest.builder()
                                                                  .userId(유저_아이디)
                                                                  .password(암호화_비빌번호)
                                                                  .name(이름)
                                                                  .birthDate(LocalDate.of(2001, 5, 12))
                                                                  .gender(Gender.FEMALE)
                                                                  .build();

    public static UserSaveRequest 유저_회원가입_요청_이름_유효성_검증실패_요청 = UserSaveRequest.builder()
                                                                             .userId(유저_아이디)
                                                                             .password(암호화_비빌번호)
                                                                             .name("")
                                                                             .birthDate(LocalDate.of(2001, 5, 12))
                                                                             .gender(Gender.FEMALE)
                                                                             .build();

    public static UserSaveRequest 유저_회원가입_요청_아이디_누락_유효성_검증실패_요청 = UserSaveRequest.builder()
                                                                                 .userId("")
                                                                                 .password(암호화_비빌번호)
                                                                                 .name(이름)
                                                                                 .birthDate(LocalDate.of(2001, 5, 12))
                                                                                 .gender(Gender.FEMALE)
                                                                                 .build();

    public static UserSaveRequest 유저_회원가입_요청_아이디_길이_유효성_검증실패_요청 = UserSaveRequest.builder()
                                                                                 .userId(유저_아이디_길이_문제)
                                                                                 .password(암호화_비빌번호)
                                                                                 .name(이름)
                                                                                 .birthDate(LocalDate.of(2001, 5, 12))
                                                                                 .gender(Gender.FEMALE)
                                                                                 .build();

    public static UserSaveRequest 유저_회원가입_요청_비밀번호_누락_유효성_검증실패_요청 = UserSaveRequest.builder()
                                                                                  .userId(유저_아이디)
                                                                                  .password("")
                                                                                  .name(이름)
                                                                                  .birthDate(LocalDate.of(2001, 5, 12))
                                                                                  .gender(Gender.FEMALE)
                                                                                  .build();

    public static UserSaveRequest 유저_회원가입_요청_비밀번호_길이_유효성_검증실패_요청 = UserSaveRequest.builder()
                                                                                  .userId(유저_아이디)
                                                                                  .password(비빌번호_길이_문제)
                                                                                  .name(이름)
                                                                                  .birthDate(LocalDate.of(2001, 5, 12))
                                                                                  .gender(Gender.FEMALE)
                                                                                  .build();

    public static UserSaveRequest 유저_회원가입_요청_비밀번호_정규식_검증실패_요청 = UserSaveRequest.builder()
                                                                               .userId(유저_아이디)
                                                                               .password(비빌번호_정규식_문제)
                                                                               .name(이름)
                                                                               .birthDate(LocalDate.of(2001, 5, 12))
                                                                               .gender(Gender.FEMALE)
                                                                               .build();

    public static UserResponse 유저_응답 = UserResponse.from(유저);

    public static DuplicatedUserIdException 유저_아이디_중복_예외 = new DuplicatedUserIdException("test");
    public static IllegalArgumentException 유저_이름_유효성_검증실패_예외메시지 = new IllegalArgumentException("이름을 입력해주세요.");
    public static IllegalArgumentException 유저_아이디_누락_유효성_검증실패_예외메시지 = new IllegalArgumentException("아이디를 입력해주세요.");
    public static IllegalArgumentException 유저_아이디_길이_유효성_검증실패_예외메시지 = new IllegalArgumentException("아이디는 6자 이상 10자 이하로 입력해주세요.");
    public static IllegalArgumentException 유저_비밀번호_누락_검증실패_예외메시지 = new IllegalArgumentException("비밀번호를 입력해주세요.");
    public static IllegalArgumentException 유저_비밀번호_길이_유효성_검증실패_예외메시지 = new IllegalArgumentException("비밀번호는 8자 이상 15자 이하로 입력해주세요.");
    public static IllegalArgumentException 유저_비밀번호_정규식_유효성_검증실패_예외메시지 = new IllegalArgumentException("비밀번호는 8~15자 길이여야 하며, 최소 1개의 영문 대소문자, 숫자, 그리고 특수문자를 포함해야 합니다.");

}
