package com.example.demo.audit.service;


import com.example.demo.audit.dto.response.AuditLogResponse;
import com.example.demo.audit.entity.AuditLogEntity;
import com.example.demo.audit.repository.AuditLogRepository;
import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.repository.URepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private URepository userRepository;

    @Override
    public Page<AuditLogResponse> getAllAuditLogs(
            String action,
            String entity,
            Integer userId,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Specification<AuditLogEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (action != null && !action.trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("action")), action.trim().toUpperCase()));
            }

            if (entity != null && !entity.trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("entity")), entity.trim().toUpperCase()));
            }

            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }

            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }

            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return auditLogRepository.findAll(specification, pageable).map(this::mapToResponse);
    }

    @Override
    public void log(Integer actorUserId, String action, String entity, Integer entityId, String detail) {
        AuditLogEntity auditLog = new AuditLogEntity();

        if (actorUserId != null) {
            UserEntity actor = userRepository.findById(actorUserId).orElse(null);
            auditLog.setUser(actor);
        }

        auditLog.setAction(action != null ? action.trim().toUpperCase() : null);
        auditLog.setEntity(entity != null ? entity.trim().toUpperCase() : null);
        auditLog.setEntityId(entityId);
        auditLog.setDetail(detail);

        auditLogRepository.save(auditLog);
    }

    private AuditLogResponse mapToResponse(AuditLogEntity entity) {
        Integer userId = null;
        String username = null;

        if (entity.getUser() != null) {
            userId = entity.getUser().getId();
            username = entity.getUser().getUsername();
        }

        return new AuditLogResponse(
                entity.getId(),
                userId,
                username,
                entity.getAction(),
                entity.getEntity(),
                entity.getEntityId(),
                entity.getCreatedAt(),
                entity.getDetail()
        );
    }
}