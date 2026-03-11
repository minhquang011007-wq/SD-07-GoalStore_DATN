package com.example.demo.audit.service;

import com.example.demo.audit.dto.response.*;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {
    Page<AuditLogResponse> getAllAuditLogs(
            String action, String entity, Integer userId, LocalDateTime from, LocalDateTime to,
            int page, int size
    );

    void log(Integer actorUserId, String action, String entity, Integer entityId, String detail);

    void logSystem(String action, String entity, Integer entityId, String detail);

    void logOrderAction(Integer actorUserId, Integer orderId, String action, String detail);

    void logCustomerAction(Integer actorUserId, Integer customerId, String action, String detail);

    Page<LoginAttemptResponse> getLoginHistory(String username, LocalDateTime from, LocalDateTime to, int page, int size);

    void recordLoginAttempt(String username, Integer userId, boolean success, String ipAddress, String userAgent);

    AuditDashboardResponse getDashboard(LocalDateTime from, LocalDateTime to);

    List<AuditReportItemResponse> reportByModule(LocalDateTime from, LocalDateTime to);

    List<AuditReportItemResponse> reportByUser(LocalDateTime from, LocalDateTime to);

    String exportCsv(String action, String entity, Integer userId, LocalDateTime from, LocalDateTime to);
}