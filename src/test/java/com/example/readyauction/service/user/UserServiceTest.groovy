package com.example.readyauction.service.user

import com.example.readyauction.controller.request.user.PasswordUpdateRequest
import com.example.readyauction.controller.request.user.UserSaveRequest
import com.example.readyauction.controller.response.user.PasswordUpdateResponse
import com.example.readyauction.controller.response.user.UserSaveResponse
import com.example.readyauction.domain.user.User
import com.example.readyauction.exception.user.DuplicatedUserIdException
import com.example.readyauction.repository.UserRepository
import spock.lang.Specification

class UserServiceTest extends Specification {
    final OK_USER_NAME = "테스트"
    final OK_USER_ID = "TEST123"
    final OK_USER_PASSWORD = "testPassword1!"

    final FAIL_USER_NAME = ""
    final FAIL_USER_ID = "FAIL"
    final FAIL_LENGTH_USER_PASSWORD = "FAIL"
    final FAIL_REGEX_USER_PASSWORD = "testPassword1"


    UserRepository userRepository = Mock()
    UserService userService = new UserService(userRepository)

    def "회원가입 성공"() {
        given:
        UserSaveRequest userSaveRequest = createUserSaveRequest()
        userRepository.findByUserId(userSaveRequest.getUserId()) >> Optional.empty()
        userRepository.save(_) >> userSaveRequest.toEntity()

        when:
        UserSaveResponse userSaveResponse = userService.join(userSaveRequest)

        then:
        userSaveResponse.getUserId() == userSaveRequest.getUserId()
        userSaveResponse.getName() == userSaveRequest.getName()
    }

    def "이름 누락_회원가입 검증 실패"() {
        given:
        UserSaveRequest userSaveRequest = UserSaveRequest.builder()
                .name(FAIL_USER_NAME)
                .userId(OK_USER_ID)
                .password(OK_USER_PASSWORD)
                .build()
        userRepository.findByUserId(userSaveRequest.getUserId()) >> Optional.empty()
        userRepository.save(_) >> userSaveRequest.toEntity()

        when:
        userService.join(userSaveRequest)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "이름을 입력해주세요."
    }

    def "아이디_회원가입 검증 실패"() {
        given:
        UserSaveRequest userSaveRequest = UserSaveRequest.builder()
                .name(OK_USER_NAME)
                .userId(FAIL_USER_ID)
                .password(OK_USER_PASSWORD)
                .build()
        userRepository.findByUserId(userSaveRequest.getUserId()) >> Optional.empty()
        userRepository.save(_) >> userSaveRequest.toEntity()

        when:
        userService.join(userSaveRequest)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "아이디는 6자 이상 10자 이하로 입력해주세요."
    }

    def "비밀번호 길이_회원가입 검증 실패"() {
        given:
        UserSaveRequest userSaveRequest = UserSaveRequest.builder()
                .name(OK_USER_NAME)
                .userId(OK_USER_ID)
                .password(FAIL_LENGTH_USER_PASSWORD)
                .build()
        userRepository.findByUserId(userSaveRequest.getUserId()) >> Optional.empty()
        userRepository.save(_) >> userSaveRequest.toEntity()

        when:
        userService.join(userSaveRequest)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "비밀번호는 8자 이상 15자 이하로 입력해주세요."
    }

    def "비밀번호 형식_회원가입 검증 실패"() {
        given:
        UserSaveRequest userSaveRequest = UserSaveRequest.builder()
                .name(OK_USER_NAME)
                .userId(OK_USER_ID)
                .password(FAIL_REGEX_USER_PASSWORD)
                .build()
        userRepository.findByUserId(userSaveRequest.getUserId()) >> Optional.empty()
        userRepository.save(_) >> userSaveRequest.toEntity()

        when:
        userService.join(userSaveRequest)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "영문 대소문자와 숫자, 특수기호가 1개씩 포함되어있는 8~15자 비밀번호입니다."
    }

    def "회원가입 중복"() {
        given:
        User existUser = User.builder()
                .name(OK_USER_NAME)
                .userId(OK_USER_ID)
                .encodedPassword(OK_USER_PASSWORD)
                .build()
        userRepository.save(_) >> existUser
        userRepository.findByUserId(OK_USER_ID) >> Optional.of(existUser)

        UserSaveRequest request = createUserSaveRequest()

        when:
        userService.join(request)

        then:
        def e = thrown(DuplicatedUserIdException)
        e.message == "${OK_USER_ID}: 이미 등록된 아이디입니다."
    }


    def "비밀번호 수정 성공"() {
        given:
        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest("newPassword1!")
        User user = User.builder()
                .userId(OK_USER_ID)
                .name(OK_USER_NAME)
                .encodedPassword(OK_USER_PASSWORD)
                .build()
        userRepository.findByUserId(user.getUserId()) >> Optional.of(user)

        when:
        PasswordUpdateResponse passwordUpdateResponse = userService.updatePassword(passwordUpdateRequest, user.getUserId())

        then:
        passwordUpdateResponse.userId == user.getUserId()
    }

    def "길이_비밀번호 수정 실패"() {
        given:
        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest("newPwd")
        User user = User.builder()
                .userId(OK_USER_ID)
                .name(OK_USER_NAME)
                .encodedPassword(OK_USER_PASSWORD)
                .build()
        userRepository.findByUserId(user.getUserId()) >> Optional.of(user)

        when:
        userService.updatePassword(passwordUpdateRequest, user.getUserId())

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "비밀번호는 8자 이상 15자 이하로 입력해주세요."
    }

    def "형식_비밀번호 수정 실패"() {
        given:
        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest("newPassword")
        User user = User.builder()
                .userId(OK_USER_ID)
                .name(OK_USER_NAME)
                .encodedPassword(OK_USER_PASSWORD)
                .build()
        userRepository.findByUserId(user.getUserId()) >> Optional.of(user)

        when:
        userService.updatePassword(passwordUpdateRequest, user.getUserId())

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "영문 대소문자와 숫자, 특수기호가 1개씩 포함되어있는 8~15자 비밀번호입니다."
    }

    private UserSaveRequest createUserSaveRequest() {
        return UserSaveRequest.builder()
                .userId(OK_USER_ID)
                .name(OK_USER_NAME)
                .password(OK_USER_PASSWORD)
                .build();
    }
}
