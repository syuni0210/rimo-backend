package com.ansim.backend.controller;

import com.ansim.backend.dto.ProfileResponseDto;
import com.ansim.backend.dto.ProfileUpdateRequestDto;
import com.ansim.backend.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{memberId}/profile")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getProfile(memberId));
    }

    @PutMapping("/{memberId}/profile")
    public ResponseEntity<Void> updateProfile(
            @PathVariable Long memberId,
            @RequestBody ProfileUpdateRequestDto request
    ) {
        memberService.updateProfile(memberId, request);
        return ResponseEntity.ok().build();
    }
}
