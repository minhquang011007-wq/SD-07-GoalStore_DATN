package com.example.demo.order_return.service;

import com.example.demo.order_return.entity.AuditLog;
import com.example.demo.order_return.entity.User;
import com.example.demo.order_return.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(User user, String action, String entity, Integer entityId, String detail) {
        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .entity(entity)
                .entityId(entityId)
                .createdAt(LocalDateTime.now())
                .detail(detail)
                .build();

        auditLogRepository.save(log);
    }
}