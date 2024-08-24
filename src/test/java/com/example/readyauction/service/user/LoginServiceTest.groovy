package com.example.readyauction.service.user

import com.example.readyauction.controller.request.user.LoginRequest
import com.example.readyauction.domain.user.CustomUserDetails
import com.example.readyauction.domain.user.User
import com.example.readyauction.exception.user.LoginFailException
import com.example.readyauction.exception.user.NotFoundUserException
import com.example.readyauction.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification

class LoginServiceTest extends Specification {
    BCryptPasswordEncoder bCryptPasswordEncoder = Mock()
    UserRepository userRepository = Mock()
    LoginService loginService = new LoginService(userRepository, bCryptPasswordEncoder)

    final String OK_USER_ID = "테스트"
    final String OK_PASSWORD = "TestPwd123!"
    final String FAIL_PASSWORD = "FAIL123!!"

    String encodedPassword = new BCryptPasswordEncoder().encode(OK_PASSWORD)

    private User createUser(String userId) {
        return User.builder()
                .userId(userId)
                .name("테스트")
                .encodedPassword(encodedPassword)
                .build()
    }

    def "로그인_성공"() {
        given:
        User user = createUser(OK_USER_ID)
        userRepository.save(user) >> user

        LoginRequest loginRequest = ["userId": OK_USER_ID, "password": OK_PASSWORD]
        userRepository.findByUserId(loginRequest.getUserId()) >> Optional.of(user)
        bCryptPasswordEncoder.matches(_, _) >> { args ->
            return args[0] == OK_PASSWORD && args[1] == user.getEncodedPassword()
        }
        when:
        def response = loginService.login(loginRequest)

        then:
        OK_USER_ID == response
    }

    def "존재하지 않는 회원이면 로그인 실패"() {
        given:
        User user = createUser(OK_USER_ID)

        LoginRequest loginRequest = ["userId": OK_USER_ID, "password": OK_PASSWORD]
        userRepository.findByUserId(loginRequest.getUserId()) >> _
        bCryptPasswordEncoder.matches(_, _) >> { args ->
            return args[0] == OK_PASSWORD && args[1] == user.getEncodedPassword()
        }
        when:
        def response = loginService.login(loginRequest)

        then:
        def e = thrown(LoginFailException)
        e.message == OK_USER_ID + ": 로그인 실패했습니다."
    }

    def "ID 또는 PWD 잘못 입력으로 인한 로그인 실패"() {
        given:
        User user = createUser(OK_USER_ID)
        userRepository.save(user) >> user

        LoginRequest loginRequest = ["userId": OK_USER_ID, "password": FAIL_PASSWORD]
        userRepository.findByUserId(loginRequest.getUserId()) >> _
        bCryptPasswordEncoder.matches(_, _) >> { args ->
            return args[0] == OK_PASSWORD && args[1] == user.getEncodedPassword()
        }
        when:
        def response = loginService.login(loginRequest)

        then:
        def e = thrown(LoginFailException)
        e.message == OK_USER_ID + ": 로그인 실패했습니다."
    }

    def "loadUserByUsername_사용자 존재 시 반환"() {
        given:
        User user = createUser(OK_USER_ID)
        userRepository.save(user)
        userRepository.findByUserId(OK_USER_ID) >> Optional.of(user)

        when:
        UserDetails userDetails = loginService.loadUserByUsername(OK_USER_ID)

        then:
        userDetails.username == OK_USER_ID
        userDetails instanceof CustomUserDetails
    }

    def "loadUserByUsername_사용자 존재하지 않을 시 예외 던짐"() {
        given:
        userRepository.findByUserId(OK_USER_ID) >> Optional.empty()

        when:
        loginService.loadUserByUsername(OK_USER_ID)

        then:
        def e = thrown(NotFoundUserException)
        e.message == OK_USER_ID + ": 존재하지 않는 회원입니다."
    }
}
