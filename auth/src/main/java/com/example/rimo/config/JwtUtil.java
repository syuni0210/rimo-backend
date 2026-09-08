package com.example.rimo.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException; // ⭐️ 예외 처리를 위해 추가된 import
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String secretKey;

    private final long ACCESS_TOKEN_EXPIRATION =
            1000L * 60 * 60; // 1시간

    private final long REFRESH_TOKEN_EXPIRATION =
            1000L * 60 * 60 * 24 * 14; // 14일

    public JwtUtil(
            @Value("${JWT_SECRET}") String secretKey
    ) {
        this.secretKey = secretKey;
    }

    public String generateAccessToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + ACCESS_TOKEN_EXPIRATION
                        )
                )
                .signWith(
                        SignatureAlgorithm.HS256,
                        secretKey
                )
                .compact();
    }

    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + REFRESH_TOKEN_EXPIRATION
                        )
                )
                .signWith(
                        SignatureAlgorithm.HS256,
                        secretKey
                )
                .compact();
    }

    public String getUserIdFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ⭐️ 새롭게 추가된 1차 방어막 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);
            return true; // 에러 없이 파싱되면 정상 토큰
        } catch (JwtException | IllegalArgumentException e) {
            // 만료되었거나, 위조되었거나, 빈 토큰일 경우 서버가 죽지 않고 false 반환
            return false; 
        }
    }
}
