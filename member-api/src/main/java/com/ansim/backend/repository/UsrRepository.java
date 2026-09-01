package com.ansim.backend.repository;

import com.ansim.backend.entity.Usr;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsrRepository extends JpaRepository<Usr, Long> {

    Optional<Usr> findByLoginId(String loginId);

    List<Usr> findByMemberNameContaining(String memberName);

    boolean existsByMmbrId(Long mmbrId);
}
