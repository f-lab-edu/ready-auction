package com.example.readyauction.service.user

import com.example.readyauction.controller.request.user.PasswordUpdateRequest
import com.example.readyauction.controller.request.user.UserSaveRequest
import com.example.readyauction.controller.response.user.PasswordUpdateResponse
import com.example.readyauction.controller.response.user.UserSaveResponse
import com.example.readyauction.domain.user.User
import com.example.readyauction.exception.user.DuplicatedUserIdException
import com.example.readyauction.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification

class UserServiceTest extends Specification {

    UserRepository userRepository = Mock(UserRepository)
    UserService userService = new UserService(userRepository)
    User testUser = new User("userId", "name", "password")


    def "회원가입_성공"() {
        given:
        userRepository.findByUserId("userId") >> Optional.empty()
        UserSaveRequest userSaveRequest = createUserSaveRequest()

        when:
        UserSaveResponse userSaveResponse = userService.join(userSaveRequest)

        then:
        1 * userService.checkedDuplicatedUser(userSaveRequest)
        userSaveResponse.getUserId() == userSaveRequest.getUserId()
        userSaveResponse.getName() == userSaveRequest.getName()
    }

    def "회원가입_중복"() {
        given:
        UserSaveRequest userSaveRequest = createUserSaveRequest()
        userRepository.save(testUser)
        userRepository.findByUserId("userId") >> Optional.of(testUser)

        when:
        userService.join(userSaveRequest)

        then:
        thrown DuplicatedUserIdException
    }

    PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest("newPassword")

    def "비밀번호_수정"() {
        given:
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        userRepository.findByUserId("userId") >> Optional.of(testUser)

        when:
        PasswordUpdateResponse passwordUpdateResponse = userService.updatePassword(passwordUpdateRequest, "userId")

        then:
        passwordUpdateResponse.userId == "userId"
        bCryptPasswordEncoder.matches("newPassword", testUser.encodedPassword)
    }

    private UserSaveRequest createUserSaveRequest() {
        return UserSaveRequest.builder()
                .userId("test")
                .name("테스트")
                .password("test")
                .build();
    }
}
