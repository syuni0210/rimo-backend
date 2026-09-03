package com.example.rimo.controller;

import com.example.rimo.dto.AuthDto;
import com.example.rimo.repository.UserRepository;
import com.example.rimo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping("/check-id")
    public ResponseEntity<AuthDto.CheckIdResponse> checkId(@RequestParam String userId) {
        boolean exists = userRepository.existsByLgnId(userId);
        if (exists) {
            return ResponseEntity.ok(new AuthDto.CheckIdResponse(false, "이미 사용 중인 아이디입니다."));
        }
        return ResponseEntity.ok(new AuthDto.CheckIdResponse(true, "사용 가능한 아이디입니다."));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid AuthDto.SignupRequest request) {
        if (userRepository.existsByLgnId(request.getUserId())) {
            return ResponseEntity.badRequest().body("이미 존재하는 아이디입니다.");
        }
        authService.signup(request);
        return ResponseEntity.ok().body("{\"message\": \"회원가입이 완료되었습니다.\"}");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDto.LoginRequest request) {
        try {
            AuthDto.LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 없습니다.");
        }
        
        // "Bearer " 문자열을 제거하고 순수 토큰 값만 추출
        String refreshToken = authHeader.substring(7);
        
        try {
            AuthDto.LoginResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 재발급 실패: " + e.getMessage());
        }
    }
}
