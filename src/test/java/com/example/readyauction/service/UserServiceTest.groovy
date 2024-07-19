package com.example.readyauction.service

import com.example.readyauction.controller.request.user.UserSaveRequest
import com.example.readyauction.controller.response.user.UserSaveResponse
import com.example.readyauction.domain.user.User
import com.example.readyauction.repository.UserRepository
import spock.lang.Specification

class UserServiceTest extends Specification {

    def "회원가입_성공"() {
        given:
        UserSaveRequest userSaveRequest = createUserSaveRequest()
        UserRepository userRepository = Mock(UserRepository)
        UserService userService = new UserService(userRepository)
        userRepository.save(_) >> userSaveRequest.toEntity()

        when:
        UserSaveResponse userSaveResponse = userService.join(userSaveRequest)

        then:
        userSaveResponse != null
        userSaveResponse.getUserId() == userSaveRequest.getUserId()
        userSaveResponse.getName() == userSaveRequest.getName()
    }
    private User user(){
        return UserSaveRequest.builder()
                .userId("test")
                .name("테스트")
                .password("test")
                .build();
    }

    private UserSaveRequest createUserSaveRequest(){
        return UserSaveRequest.builder()
                .userId("test")
                .name("테스트")
                .password("test")
                .build();
    }
}
