package com.example.readyauction.service

import com.example.readyauction.controller.request.user.PasswordUpdateRequest
import com.example.readyauction.controller.request.user.UserSaveRequest
import com.example.readyauction.controller.response.user.PasswordUpdateResponse
import com.example.readyauction.controller.response.user.UserSaveResponse
import com.example.readyauction.domain.user.User
import com.example.readyauction.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification

class UserServiceTest extends Specification {

    UserRepository userRepository = Mock(UserRepository)
    UserService userService = new UserService(userRepository)
    UserSaveRequest userSaveRequest = createUserSaveRequest()

    def "회원가입_성공"() {
        given:
        userRepository.save(_) >> userSaveRequest.toEntity()

        when:
        UserSaveResponse userSaveResponse = userService.join(userSaveRequest)

        then:
        userSaveResponse.getUserId() == userSaveRequest.getUserId()
        userSaveResponse.getName() == userSaveRequest.getName()
    }

    PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest("newPassword")

    def "비밀번호_수정"() {
        given:
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User tesUser = new User("userId", "name", "password")
        userRepository.findByUserId("userId") >> Optional.of(tesUser)

        when:
        PasswordUpdateResponse passwordUpdateResponse = userService.updatePassword(passwordUpdateRequest, "userId")

        then:
        passwordUpdateResponse.userId == "userId"
        bCryptPasswordEncoder.matches("newPassword", tesUser.encodedPassword)
    }

    private UserSaveRequest createUserSaveRequest() {
        return UserSaveRequest.builder()
                .userId("test")
                .name("테스트")
                .password("test")
                .build();
    }
}
