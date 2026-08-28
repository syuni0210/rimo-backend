package com.ansim.backend.dto;

public class ProfileResponseDto {
    private Long memberId;
    private String loginId;
    private String memberName;
    private String email;

    public ProfileResponseDto(Long memberId, String loginId, String memberName, String email) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.memberName = memberName;
        this.email = email;
    }

    public Long getMemberId() { return memberId; }
    public String getLoginId() { return loginId; }
    public String getMemberName() { return memberName; }
    public String getEmail() { return email; }
}
