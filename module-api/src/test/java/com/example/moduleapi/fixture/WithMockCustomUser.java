package com.example.moduleapi.fixture;

import com.example.moduledomain.domain.user.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    long id() default 1L;

    String userId() default "test0512";

    String encodedPwd() default "TestPwd123";

    String name() default "테스트";

    Role role() default Role.ROLE_USER;
}
