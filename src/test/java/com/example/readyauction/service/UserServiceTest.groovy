package com.example.readyauction.service

import com.example.readyauction.controller.request.user.PasswordUpdateRequest
import com.example.readyauction.controller.request.user.UserSaveRequest
import com.example.readyauction.controller.response.user.PasswordUpdateResponse
import com.example.readyauction.controller.response.user.UserSaveResponse
import com.example.readyauction.domain.user.User
import com.example.readyauction.repository.UserRepository
import org.mockito.Mock
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Shared
import spock.lang.Specification

class UserServiceTest extends Specification {

    UserRepository userRepository = Mock(UserRepository)
    UserService userService = new UserService(userRepository)

    def "회원가입_성공"() {
        given:
        UserSaveRequest userSaveRequest = createUserSaveRequest()
        userRepository.save(_) >> userSaveRequest.toEntity()

        when:
        UserSaveResponse userSaveResponse = userService.join(userSaveRequest)

        then:
        userSaveResponse != null
        userSaveResponse.getUserId() == userSaveRequest.getUserId()
        userSaveResponse.getName() == userSaveRequest.getName()
    }

    def "비밀번호_수정"(){
        given:
        String userId = "userId"
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode("oldPassword")
        User tesUser = new User(1L, userId, "name", encodedPassword, "주소")

        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest("newPassword")
        userRepository.findByUserId(userId) >> Optional.of(tesUser)

        when:
        PasswordUpdateResponse passwordUpdateResponse = userService.updatePassword(passwordUpdateRequest, userId)

        then:
        passwordUpdateResponse.userId == userId
        passwordUpdateResponse.msg == "success"
        bCryptPasswordEncoder.matches("newPassword",tesUser.encodedPassword)
    }

    private UserSaveRequest createUserSaveRequest(){
        return UserSaveRequest.builder()
                .userId("test")
                .name("테스트")
                .password("test")
                .build();
    }
}
