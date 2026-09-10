package com.example.rimo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor // ⭐️ JwtAuthenticationFilter 의존성 주입을 위해 추가
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // ⭐️ 추가됨

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 로그인, 회원가입 등 auth 관련 API는 토큰 없이 무조건 통과
                .requestMatchers("/api/auth/**", "/actuator/health", "/actuator/prometheus").permitAll() 
                // 그 외의 모든 API(위치 전송 등)는 무조건 인증(토큰)을 요구
                .anyRequest().authenticated() 
            )
            // ⭐️ 핵심: 스프링의 기본 인증 필터가 돌기 전에, 우리가 만든 JWT 필터를 먼저 실행!
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
