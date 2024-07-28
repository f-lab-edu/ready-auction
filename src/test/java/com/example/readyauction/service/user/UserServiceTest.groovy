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

    UserRepository userRepository = Mock()
    UserService userService = new UserService(userRepository)

    def "회원가입_성공"() {
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

    def "회원가입_중복"() {
        given:
        User user = User.builder()
                .userId("test")
                .name("test")
                .encodedPassword("test")
                .build()
        userRepository.findByUserId(user.getUserId()) >> Optional.of(user)

        when:
        userService.join(createUserSaveRequest())

        then:
        def e = thrown(DuplicatedUserIdException)
        e.message == user.getUserId() + ": 이미 등록된 아이디입니다."
    }


    def "비밀번호_수정"() {
        given:
        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest("newPassword")
        User user = User.builder()
                .userId("test")
                .name("name")
                .encodedPassword("oldPassword")
                .build()
        userRepository.findByUserId(user.getUserId()) >> Optional.of(user)

        when:
        PasswordUpdateResponse passwordUpdateResponse = userService.updatePassword(passwordUpdateRequest, user.getUserId())

        then:
        passwordUpdateResponse.userId == user.getUserId()
    }

    private UserSaveRequest createUserSaveRequest() {
        return UserSaveRequest.builder()
                .userId("test")
                .name("test")
                .password("test")
                .build();
    }
}
