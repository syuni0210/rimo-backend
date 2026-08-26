package com.example.anonbackend.service;

import com.example.anonbackend.dto.ProfileResponseDto;
import com.example.anonbackend.dto.ProfileUpdateRequestDto;
import com.example.anonbackend.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public ProfileResponseDto getProfile(Long memberId) {

        return memberRepository
                .findProfileByMemberId(memberId);
    }

    public void updateProfile(
            Long memberId,
            ProfileUpdateRequestDto request
    ) {

        memberRepository.updateProfile(
                memberId,
                request.getMemberName(),
                request.getEmail()
        );
    }
}
