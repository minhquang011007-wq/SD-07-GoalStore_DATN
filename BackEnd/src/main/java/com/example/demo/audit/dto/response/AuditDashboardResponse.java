package com.example.demo.audit.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@Builder
public class AuditDashboardResponse {
    private long totalLogs;
    private long totalLoginSuccess;
    private long totalLoginFailed;
    private long totalAlerts;
    private long totalCriticalAlerts;
    private long sensitiveActions;
    private Map<String, Long> logsByModule;
    private Map<String, Long> logsByAction;
}