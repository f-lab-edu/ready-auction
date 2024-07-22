package com.example.readyauction.service.user

import com.example.readyauction.controller.request.user.LoginRequest
import com.example.readyauction.domain.user.User
import com.example.readyauction.exception.user.LoginFailException
import com.example.readyauction.repository.UserRepository
import jakarta.servlet.http.HttpSession
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification

class LoginServiceTest extends Specification {
    HttpSession httpSession = new MockHttpSession()
    UserRepository userRepository = Mock(UserRepository)
    LoginService loginService = new LoginService(httpSession, userRepository)

    String userId = "testUser"
    String rawPassword = "testPassword"
    String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword)
    User tesUser = new User(userId, "name", encodedPassword)

    def "로그인_성공"() {
        given:
        userRepository.findByUserId(userId) >> Optional.of(tesUser)

        when:
        loginService.login(new LoginRequest(userId, rawPassword))

        then:
        httpSession.getAttribute(LoginService.USER_ID) == userId

    }

    def "로그인_실패"() {
        given:
        userRepository.findByUserId(userId) >> Optional.of(tesUser)

        when:
        loginService.login(new LoginRequest(userId, "FailPassword"))

        then:
        thrown LoginFailException
    }

    def "로그아웃_성공"() {
        when:
        loginService.logout()
        then:
        1 * httpSession.invalidate()
    }
}
