package com.example.readyauction.fixture;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.example.readyauction.domain.user.CustomUserDetails;
import com.example.readyauction.domain.user.User;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = User.builder()
            .userId(customUser.userId())
            .name(customUser.name())
            .encodedPassword(customUser.encodedPwd())
            .build();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(customUserDetails,
            customUserDetails.getPassword(), customUserDetails.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
