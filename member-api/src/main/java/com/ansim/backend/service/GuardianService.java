package com.ansim.backend.service;

import com.ansim.backend.entity.Guardian;
import com.ansim.backend.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuardianService {

    private final GuardianRepository guardianRepository;

    public Guardian registerGuardian(Long memberId, String guardianName, String phoneNumber, String relationName) {
        Guardian guardian = new Guardian();
        guardian.setMemberId(memberId);
        guardian.setGuardianName(guardianName);
        guardian.setPhoneNumber(phoneNumber);
        guardian.setRelationName(relationName);
        guardian.setRegisteredAt(LocalDateTime.now());
        guardian.setUseYn("Y");
        return guardianRepository.save(guardian);
    }

    public List<Guardian> getGuardians(Long memberId) {
        return guardianRepository.findByMemberIdAndUseYn(memberId, "Y");
    }

    public Guardian updateGuardian(Long guardianId, String guardianName, String phoneNumber, String relationName) {
        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new IllegalArgumentException("보호자를 찾을 수 없습니다: " + guardianId));
        guardian.setGuardianName(guardianName);
        guardian.setPhoneNumber(phoneNumber);
        guardian.setRelationName(relationName);
        return guardianRepository.save(guardian);
    }

    public void deleteGuardian(Long guardianId) {
        if (!guardianRepository.existsById(guardianId)) {
            throw new IllegalArgumentException("보호자를 찾을 수 없습니다: " + guardianId);
        }
        guardianRepository.deleteById(guardianId);
    }
}
