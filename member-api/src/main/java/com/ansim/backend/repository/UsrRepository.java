package com.ansim.backend.repository;

import com.ansim.backend.entity.Usr;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsrRepository extends JpaRepository<Usr, Long> {

    Optional<Usr> findByLoginIdAndUseYnAndDeleteYn(String loginId, String useYn, String deleteYn);

    List<Usr> findByMemberNameContainingAndUseYnAndDeleteYn(String memberName, String useYn, String deleteYn);

    boolean existsByMmbrIdAndUseYnAndDeleteYn(Long mmbrId, String useYn, String deleteYn);
}
