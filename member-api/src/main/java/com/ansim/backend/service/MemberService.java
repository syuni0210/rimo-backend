package com.ansim.backend.service;

import com.ansim.backend.dto.ProfileResponseDto;
import com.ansim.backend.dto.ProfileUpdateRequestDto;
import com.ansim.backend.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public ProfileResponseDto getProfile(Long memberId) {
        return memberRepository.findProfileByMemberId(memberId);
    }

    public void updateProfile(Long memberId, ProfileUpdateRequestDto request) {
        memberRepository.updateProfile(memberId, request.getMemberName(), request.getEmail());
    }
}
