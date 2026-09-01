package com.example.rimo.repository;

import com.example.rimo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLgnId(String lgnId);
    boolean existsByLgnId(String lgnId);
}
