package com.example.rimo.service;

import com.example.rimo.dto.AuthDto;
import com.example.rimo.entity.User;
import com.example.rimo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // JWT 서명용 임시 키 (실무에서는 yml 파일에 보관합니다)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public void signup(AuthDto.SignupRequest request) {
        User user = new User();
        user.setLgnId(request.getUserId());
        user.setPwd(passwordEncoder.encode(request.getPassword())); // BCrypt 암호화
        user.setMmbrNm(request.getName());
        user.setEmail(request.getEmail());
        userRepository.save(user);
    }
   public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findByLgnId(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPwd())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
    }

    // 💡 1. 기존에 쓰시던 토큰 생성 로직 (이 코드는 절대 안 건드렸습니다!)
        String token = Jwts.builder()
            .setSubject(user.getLgnId())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 300000))
            .signWith(key)
            .compact();

    // 💡 2. 토큰과 함께 memberId와 사용자 이름(user.getMmbrNm())을 같이 담아서 리턴!
        return new AuthDto.LoginResponse(
            "로그인 성공",
            token,
            user.getMmbrId(),
            user.getMmbrNm() 
    );
}
}
