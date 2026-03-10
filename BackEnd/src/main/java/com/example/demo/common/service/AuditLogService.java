package com.example.demo.common.service;

import com.example.demo.common.dto.response.AuditLogResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface AuditLogService {

    Page<AuditLogResponse> getAllAuditLogs(
            String action,
            String entity,
            Integer userId,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size
    );

    void log(Integer actorUserId, String action, String entity, Integer entityId, String detail);
}