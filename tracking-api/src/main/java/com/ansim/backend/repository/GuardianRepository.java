package com.ansim.backend.repository;

import com.ansim.backend.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    List<Guardian> findByMemberIdAndUseYn(Long memberId, String useYn);
}
