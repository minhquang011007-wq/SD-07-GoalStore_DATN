package com.example.demo.audit.repository;

import com.example.demo.audit.entity.LoginAttemptEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface LoginAttemptRepository extends JpaRepository<LoginAttemptEntity, Long> {
    Page<LoginAttemptEntity> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<LoginAttemptEntity> findByUsernameContainingIgnoreCaseAndCreatedAtBetween(
            String username, LocalDateTime from, LocalDateTime to, Pageable pageable
    );

    long countByUsernameAndSuccessIsFalseAndCreatedAtAfter(String username, LocalDateTime after);

    long countByIpAddressAndSuccessIsFalseAndCreatedAtAfter(String ipAddress, LocalDateTime after);
}