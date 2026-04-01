package com.example.demo.order_return.service;

import com.example.demo.audit.entity.AuditLogEntity;
import com.example.demo.audit.repository.AuditLogRepository;
import com.example.demo.auth.entity.User;
import com.example.demo.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(User user, String action, String entity, Integer entityId, String detail) {

        if (user == null) {
            return;
        }

        // convert auth.User -> UserEntity
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());

        AuditLogEntity audit = new AuditLogEntity();
        audit.setUser(userEntity);
        audit.setAction(action);
        audit.setEntity(entity);
        audit.setEntityId(entityId);
        audit.setDetail(detail);

        auditLogRepository.save(audit);
    }
}