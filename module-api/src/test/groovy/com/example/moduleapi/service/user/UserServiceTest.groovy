package com.example.moduleapi.service.user

import com.example.moduleapi.controller.request.user.UserSaveRequest
import com.example.moduleapi.exception.user.DuplicatedUserIdException
import com.example.moduleapi.fixture.user.UserFixtures
import com.example.moduledomain.domain.user.User
import com.example.moduledomain.repository.user.PointRepository
import com.example.moduledomain.repository.user.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification

class UserServiceTest extends Specification {
    UserRepository userRepository = Mock()
    PointRepository pointRepository = Mock()
    BCryptPasswordEncoder passwordEncoder = Mock()

    UserService userService = new UserService(userRepository, pointRepository, passwordEncoder)

    def "회원가입 성공"() {
        given:
        UserSaveRequest request = new UserSaveRequest(
                "name": "test",
                "userId": "testId",
                "password": "Password123!"
        )
        passwordEncoder.encode("Password123!") >> "encodedPassword"
        userRepository.findByUserId("testId") >> Optional.empty()

        when:
        userService.join(request)

        then:
        1 * userRepository.save(_) >> {
            User user = it[0]
            verifyAll(user) {
                assert it.name == request.getName()
                assert it.userId == request.getUserId()
                assert it.encodedPassword == "encodedPassword"
            }
            return user
        }
    }

    def "회원가입 - 입력값 유효성 검증 실패"() {
        given:
        UserSaveRequest request = new UserSaveRequest(
                "name": name,
                "userId": userId,
                "password": password
        )
        userRepository.findByUserId("testId") >> Optional.empty()

        when:
        userService.join(request)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == expected

        where:
        name   | userId   | password       || expected
        ""     | "testId" | "Password123!" || "이름을 입력해주세요."
        "test" | ""       | "Password123!" || "아이디를 입력해주세요."
        "test" | "testId" | ""             || "비밀번호를 입력해주세요."
        "test" | "short"  | "short"        || "아이디는 6자 이상 10자 이하로 입력해주세요."
        "test" | "testId" | "short"        || "비밀번호는 8자 이상 15자 이하로 입력해주세요."
        "test" | "testId" | "1234578!"     || "비밀번호는 8~15자 길이여야 하며, 최소 1개의 영문 대소문자, 숫자, 그리고 특수문자를 포함해야 합니다."

    }

    def "회원가입 실패_중복된 UserId"() {
        given:
        UserSaveRequest request = new UserSaveRequest(
                "name": "test",
                "userId": "testId",
                "password": "Password123!"
        )
        userRepository.findByUserId("testId") >> Optional.of(UserFixtures.createUser())

        when:
        userService.join(request)

        then:
        def e = thrown(DuplicatedUserIdException)
        e.message == "testId" + ": 이미 등록된 아이디입니다."
    }
}
