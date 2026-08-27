package com.example.rimo.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

public class AuthDto {

    @Getter @Setter
    public static class SignupRequest {
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문과 숫자만 사용할 수 있습니다.")
        private String userId; // 안드로이드 요청 매핑
        private String email;
        private String password;
        private String name;
    }

    @Getter @Setter
    public static class LoginRequest {
        private String userId;
        private String password;
    }

    @Getter
    public static class LoginResponse {
        private String message;
        private String token;
	private Long memberId;
	private String name;
        
        public LoginResponse(String message, String token, Long memberId, String name) {
            this.message = message;
            this.token = token;
	    this.memberId = memberId;
	    this.name = name;
        }
    }

    @Getter
    public static class CheckIdResponse {
        private boolean isAvailable;
        private String message;

        public CheckIdResponse(boolean isAvailable, String message) {
            this.isAvailable = isAvailable;
            this.message = message;
        }
    }
}
