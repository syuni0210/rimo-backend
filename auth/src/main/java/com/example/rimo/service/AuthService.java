package com.example.rimo.service;

import com.example.rimo.dto.AuthDto;
import com.example.rimo.entity.User;
import com.example.rimo.repository.UserRepository;
import com.example.rimo.config.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.concurrent.TimeUnit;
import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate; 

    public void signup(AuthDto.SignupRequest request) {
        User user = new User();
        user.setLgnId(request.getUserId());
        user.setPwd(passwordEncoder.encode(request.getPassword())); // BCrypt 암호화
        user.setMmbrNm(request.getName());
        user.setEmail(request.getEmail());
        user.setUseYn("Y");
        user.setDeleteYn("N");
        userRepository.save(user);
    }
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findByLgnId(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPwd())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
    }

        String accessToken = jwtUtil.generateAccessToken(user.getLgnId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getLgnId());
        String redisKey = "auth:refresh:" + user.getMmbrId();
        redisTemplate.opsForValue().set(redisKey, refreshToken, 14, TimeUnit.DAYS);

        return new AuthDto.LoginResponse(
            "로그인 성공",
            accessToken,
            refreshToken,
            user.getMmbrId(),
            user.getMmbrNm() 
        );
    }
    public AuthDto.LoginResponse refreshToken(String refreshToken) {
        // 1. 토큰에서 사용자 아이디(userId) 추출
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        
        User user = userRepository.findByLgnId(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. Redis에서 해당 사용자의 Refresh Token을 꺼내와서 비교
        String redisKey = "auth:refresh:" + user.getMmbrId();
        String savedToken = redisTemplate.opsForValue().get(redisKey);

        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않거나 로그아웃된 Refresh Token 입니다.");
        }

        // 3. 검증 통과 시 새 토큰 2개 생성 및 Redis 갱신
        String newAccessToken = jwtUtil.generateAccessToken(userId);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);
        
        redisTemplate.opsForValue().set(redisKey, newRefreshToken, 14, java.util.concurrent.TimeUnit.DAYS);

        // 4. 새 토큰을 DTO에 담아 반환
        return new AuthDto.LoginResponse(
            "토큰 재발급 성공",
            newAccessToken,
            newRefreshToken,
            user.getMmbrId(),
            user.getMmbrNm()
        );
    }
}

