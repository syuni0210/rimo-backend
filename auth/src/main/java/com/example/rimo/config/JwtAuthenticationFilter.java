package com.example.rimo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 클라이언트가 보낸 HTTP 요청 헤더에서 "Authorization" 값을 뽑아냄
        String authorizationHeader = request.getHeader("Authorization");

        // 2. 헤더에 토큰이 "Bearer " 형태로 정상적으로 들어있는지 확인
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // "Bearer " 뒷부분의 진짜 토큰만 추출

            // 3. ⭐️ 우리가 만든 validateToken으로 토큰 검사
            if (!jwtUtil.validateToken(token)) {
                // 토큰이 썩었거나 위조되었다면 바로 401 에러를 던지고 쫓아냄 (컨트롤러로 안 보냄)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or Expired Token");
                return; // 필터 체인 즉시 종료
            }
        }

        // 4. 정상 토큰이거나, 로그인/회원가입처럼 토큰이 원래 없는 요청이라면 다음 단계(컨트롤러)로 통과
        filterChain.doFilter(request, response);
    }
}
