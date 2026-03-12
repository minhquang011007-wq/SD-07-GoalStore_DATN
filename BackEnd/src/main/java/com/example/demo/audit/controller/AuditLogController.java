package com.example.demo.audit.controller;

import com.example.demo.audit.dto.response.*;
import com.example.demo.audit.service.AuditLogService;
import com.example.demo.audit.service.SecurityAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")

@Tag(name = "Audit Log", description = "API audit, report, login history, alerts")
public class AuditLogController {
    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private SecurityAlertService securityAlertService;

    @Operation(summary = "Lấy danh sách audit log")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<AuditLogResponse>> getAllAuditLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entity,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                auditLogService.getAllAuditLogs(action, entity, userId, from, to, page, size)
        );
    }

    @Operation(summary = "Lấy lịch sử login")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/login-history")
    public ResponseEntity<Page<LoginAttemptResponse>> getLoginHistory(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(auditLogService.getLoginHistory(username, from, to, page, size));
    }

    @Operation(summary = "Dashboard audit")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<AuditDashboardResponse> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(auditLogService.getDashboard(from, to));
    }

    @Operation(summary = "Báo cáo log theo module")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/report/module")
    public ResponseEntity<List<AuditReportItemResponse>> reportByModule(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(auditLogService.reportByModule(from, to));
    }

    @Operation(summary = "Báo cáo log theo user")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/report/user")
    public ResponseEntity<List<AuditReportItemResponse>> reportByUser(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(auditLogService.reportByUser(from, to));
    }

    @Operation(summary = "Export log ra CSV")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export/csv")
    public ResponseEntity<ByteArrayResource> exportCsv(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entity,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        String csv = auditLogService.exportCsv(action, entity, userId, from, to);
        ByteArrayResource resource = new ByteArrayResource(csv.getBytes(StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audit-log.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @Operation(summary = "Lấy security alerts")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/alerts")
    public ResponseEntity<Page<SecurityAlertResponse>> getAlerts(
            @RequestParam(required = false) Boolean resolved,
            @RequestParam(required = false) String alertType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(securityAlertService.getAlerts(resolved, alertType, page, size));
    }

    @Operation(summary = "Resolve alert")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/alerts/{id}/resolve")
    public ResponseEntity<SecurityAlertResponse> resolveAlert(@PathVariable Long id) {
        return ResponseEntity.ok(securityAlertService.resolveAlert(id));
    }
}