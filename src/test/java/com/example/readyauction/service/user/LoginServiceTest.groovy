package com.example.readyauction.service.user

import com.example.readyauction.controller.request.user.LoginRequest
import com.example.readyauction.domain.user.User
import com.example.readyauction.exception.user.LoginFailException
import com.example.readyauction.repository.UserRepository
import jakarta.servlet.http.HttpSession
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification

class LoginServiceTest extends Specification {
    HttpSession httpSession = Mock(HttpSession)
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
        1 * httpSession.setAttribute(LoginService.USER_ID, userId)

    }

    def "로그인_실패"() {
        given:
        userRepository.findByUserId(userId) >> Optional.of(tesUser)

        when:
        loginService.login(new LoginRequest(userId, "FailPassword"))

        then:
        def e = thrown(LoginFailException)
        e.message == userId + ": 로그인 실패했습니다."
    }

    def "로그아웃_성공"() {
        when:
        loginService.logout()
        then:
        1 * httpSession.invalidate()
    }
}
