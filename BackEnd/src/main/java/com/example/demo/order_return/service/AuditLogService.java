package com.example.demo.order_return.service;

import com.example.demo.auth.entity.User;
import com.example.demo.order_return.entity.AuditLog;
import com.example.demo.order_return.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(User user, String action, String targetType, Integer targetId, String details) {
        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .targetType(targetType)
                .targetId(targetId == null ? null : Long.valueOf(targetId))
                .details(details)
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
    }
}