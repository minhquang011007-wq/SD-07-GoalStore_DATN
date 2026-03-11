package com.example.demo.audit.repository;

import com.example.demo.audit.entity.SecurityAlertEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityAlertRepository extends JpaRepository<SecurityAlertEntity, Long> {
    Page<SecurityAlertEntity> findByResolved(Boolean resolved, Pageable pageable);

    Page<SecurityAlertEntity> findByAlertTypeContainingIgnoreCase(String alertType, Pageable pageable);

    Page<SecurityAlertEntity> findByResolvedAndAlertTypeContainingIgnoreCase(
            Boolean resolved, String alertType, Pageable pageable
    );
}