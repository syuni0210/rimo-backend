package com.ansim.backend.repository;

import com.ansim.backend.entity.Emergency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyRepository extends JpaRepository<Emergency, Long> {
}
