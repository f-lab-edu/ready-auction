package com.example.readyauction.service

import com.example.readyauction.controller.request.user.UserSaveRequest
import com.example.readyauction.controller.response.user.UserSaveResponse
import com.example.readyauction.domain.user.User
import com.example.readyauction.repository.UserRepository
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Shared
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
        userSaveRequest.getAddress() == userSaveRequest.getAddress()
    }


    private UserSaveRequest createUserSaveRequest(){
        return UserSaveRequest.builder()
                .userId("test")
                .name("테스트")
                .address("서울특별시 송파구 올림픽로240 롯데월드")
                .password("test")
                .build();
    }
}
