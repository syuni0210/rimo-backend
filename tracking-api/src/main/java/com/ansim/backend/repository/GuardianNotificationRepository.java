package com.ansim.backend.repository;

import com.ansim.backend.entity.GuardianNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardianNotificationRepository extends JpaRepository<GuardianNotification, Long> {
}
