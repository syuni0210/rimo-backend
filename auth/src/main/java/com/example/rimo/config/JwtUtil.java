package com.example.rimo.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
}
