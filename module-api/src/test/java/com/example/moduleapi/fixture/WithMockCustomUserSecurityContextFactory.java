package com.example.moduleapi.fixture;

import com.example.moduledomain.domain.user.CustomUserDetails;
import com.example.moduledomain.domain.user.Role;
import com.example.moduledomain.domain.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.LocalDate;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = User.builder()
                        .userId(customUser.userId())
                        .name(customUser.name())
                        .birthDate(LocalDate.of(2001, 5, 12))
                        .role(Role.ROLE_USER)
                        .encodedPassword(customUser.encodedPwd())
                        .build();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(customUserDetails,
                customUserDetails.getPassword(), customUserDetails.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
