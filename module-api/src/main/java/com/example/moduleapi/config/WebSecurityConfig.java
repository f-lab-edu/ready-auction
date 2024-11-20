package com.example.moduleapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/auction/subscribe")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/docs/*")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/v1/users")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/v1/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/v1/logout")).permitAll()
                .anyRequest().authenticated()
        );

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1) // 동일 아이디에 대한 다중 로그인 허용 갯수
                .maxSessionsPreventsLogin(true)); // 설정한 다중 로그인 허용 갯수를 초과하였을 시 처리 방법 true(초과시 새로운 로그인 차단)

        http.logout(logout -> logout
                .logoutUrl("/api/v1/logout")
                .logoutSuccessUrl("/api/v1/login")
                .invalidateHttpSession(true)
        );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
