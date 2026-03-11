package com.example.demo.audit.service;

import com.example.demo.audit.dto.response.*;
import com.example.demo.audit.entity.AuditLogEntity;
import com.example.demo.audit.entity.LoginAttemptEntity;
import com.example.demo.audit.repository.AuditLogRepository;
import com.example.demo.audit.repository.LoginAttemptRepository;
import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.repository.URepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Autowired
    private URepository userRepository;

    @Autowired
    private SecurityAlertService securityAlertService;

    @Autowired
    private SensitiveAlertMailService sensitiveAlertMailService;

    @Override
    public Page<AuditLogResponse> getAllAuditLogs(
            String action, String entity, Integer userId, LocalDateTime from, LocalDateTime to,
            int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return auditLogRepository.findAll(buildSpec(action, entity, userId, from, to), pageable)
                .map(this::mapToResponse);
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

        if (isSensitiveAction(action)) {
            securityAlertService.createAlert(
                    actorUserId, "SENSITIVE_ACTION", "WARNING",
                    "Thao tác nhạy cảm: " + action + " | " + detail, null
            );
            sensitiveAlertMailService.sendSensitiveAlert(
                    "Cảnh báo thao tác nhạy cảm",
                    "Action=" + action + " | Detail=" + detail
            );
        }

        if ("UPDATE_ROLE_PERMISSION".equalsIgnoreCase(action) ||
                "GRANT_PERMISSION".equalsIgnoreCase(action) ||
                "REVOKE_PERMISSION".equalsIgnoreCase(action)) {
            securityAlertService.createAlert(
                    actorUserId, "ROLE_CHANGE", "WARNING",
                    "Thay đổi quyền: " + detail, null
            );
        }
    }

    @Override
    public void logSystem(String action, String entity, Integer entityId, String detail) {
        log(null, action, entity, entityId, detail);
    }

    @Override
    public void logOrderAction(Integer actorUserId, Integer orderId, String action, String detail) {
        log(actorUserId, action, "ORDER", orderId, detail);
    }

    @Override
    public void logCustomerAction(Integer actorUserId, Integer customerId, String action, String detail) {
        log(actorUserId, action, "CUSTOMER", customerId, detail);
    }

    @Override
    public Page<LoginAttemptResponse> getLoginHistory(
            String username, LocalDateTime from, LocalDateTime to, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        LocalDateTime start = from != null ? from : LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime end = to != null ? to : LocalDateTime.now();

        Page<LoginAttemptEntity> result;
        if (username != null && !username.isBlank()) {
            result = loginAttemptRepository.findByUsernameContainingIgnoreCaseAndCreatedAtBetween(
                    username.trim(), start, end, pageable
            );
        } else {
            result = loginAttemptRepository.findByCreatedAtBetween(start, end, pageable);
        }

        return result.map(this::mapLoginResponse);
    }

    @Override
    public void recordLoginAttempt(
            String username, Integer userId, boolean success, String ipAddress, String userAgent
    ) {
        LoginAttemptEntity entity = new LoginAttemptEntity();
        entity.setUsername(username);
        entity.setSuccess(success);
        entity.setIpAddress(ipAddress);
        entity.setUserAgent(userAgent);
        if (userId != null) {
            UserEntity user = userRepository.findById(userId).orElse(null);
            entity.setUser(user);
        }
        loginAttemptRepository.save(entity);

        log(userId, success ? "LOGIN_SUCCESS" : "LOGIN_FAILED", "AUTH", userId,
                "username=" + username + ", ip=" + ipAddress);

        if (!success) {
            long failedByUser = loginAttemptRepository.countByUsernameAndSuccessIsFalseAndCreatedAtAfter(
                    username, LocalDateTime.now().minusMinutes(15)
            );
            long failedByIp = ipAddress != null ?
                    loginAttemptRepository.countByIpAddressAndSuccessIsFalseAndCreatedAtAfter(
                            ipAddress, LocalDateTime.now().minusMinutes(15)) : 0;

            if (failedByUser >= 5 || failedByIp >= 7) {
                securityAlertService.createAlert(
                        userId, "UNUSUAL_ACCESS", "CRITICAL",
                        "Phát hiện nhiều lần đăng nhập thất bại username=" + username,
                        ipAddress
                );
            }
        }
    }

    @Override
    public AuditDashboardResponse getDashboard(LocalDateTime from, LocalDateTime to) {
        LocalDateTime start = from != null ? from : LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime end = to != null ? to : LocalDateTime.now();

        List<AuditLogEntity> logs = auditLogRepository.findAll(
                buildSpec(null, null, null, start, end), Sort.by("id").descending()
        );

        long totalLoginSuccess = logs.stream()
                .filter(l -> "LOGIN_SUCCESS".equalsIgnoreCase(l.getAction())).count();
        long totalLoginFailed = logs.stream()
                .filter(l -> "LOGIN_FAILED".equalsIgnoreCase(l.getAction())).count();
        long sensitiveActions = logs.stream()
                .filter(l -> isSensitiveAction(l.getAction())).count();

        Map<String, Long> logsByModule = logs.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getEntity() == null ? "UNKNOWN" : l.getEntity(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        Map<String, Long> logsByAction = logs.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getAction() == null ? "UNKNOWN" : l.getAction(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        long totalAlerts = securityAlertService.getAlerts(null, null, 0, 1).getTotalElements();
        long totalCriticalAlerts = securityAlertService.getAlerts(false, null, 0, 1000)
                .getContent()
                .stream()
                .filter(a -> "CRITICAL".equalsIgnoreCase(a.getSeverity()))
                .count();

        return AuditDashboardResponse.builder()
                .totalLogs(logs.size())
                .totalLoginSuccess(totalLoginSuccess)
                .totalLoginFailed(totalLoginFailed)
                .totalAlerts(totalAlerts)
                .totalCriticalAlerts(totalCriticalAlerts)
                .sensitiveActions(sensitiveActions)
                .logsByModule(logsByModule)
                .logsByAction(logsByAction)
                .build();
    }

    @Override
    public List<AuditReportItemResponse> reportByModule(LocalDateTime from, LocalDateTime to) {
        List<AuditLogEntity> logs = auditLogRepository.findAll(
                buildSpec(null, null, null, from, to), Sort.by("id").descending()
        );

        return logs.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getEntity() == null ? "UNKNOWN" : l.getEntity(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(e -> new AuditReportItemResponse(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(AuditReportItemResponse::getTotal).reversed())
                .toList();
    }

    @Override
    public List<AuditReportItemResponse> reportByUser(LocalDateTime from, LocalDateTime to) {
        List<AuditLogEntity> logs = auditLogRepository.findAll(
                buildSpec(null, null, null, from, to), Sort.by("id").descending()
        );

        return logs.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getUser() == null ? "SYSTEM" : l.getUser().getUsername(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(e -> new AuditReportItemResponse(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(AuditReportItemResponse::getTotal).reversed())
                .toList();
    }

    @Override
    public String exportCsv(String action, String entity, Integer userId, LocalDateTime from, LocalDateTime to) {
        List<AuditLogEntity> logs = auditLogRepository.findAll(
                buildSpec(action, entity, userId, from, to), Sort.by("id").descending()
        );

        StringBuilder sb = new StringBuilder();
        sb.append("id,userId,username,action,entity,entityId,createdAt,detail\n");

        for (AuditLogEntity log : logs) {
            Integer uid = log.getUser() != null ? log.getUser().getId() : null;
            String uname = log.getUser() != null ? log.getUser().getUsername() : "SYSTEM";
            sb.append(log.getId()).append(",")
                    .append(uid == null ? "" : uid).append(",")
                    .append(csv(uname)).append(",")
                    .append(csv(log.getAction())).append(",")
                    .append(csv(log.getEntity())).append(",")
                    .append(log.getEntityId() == null ? "" : log.getEntityId()).append(",")
                    .append(log.getCreatedAt()).append(",")
                    .append(csv(log.getDetail()))
                    .append("\n");
        }

        return sb.toString();
    }

    private String csv(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private boolean isSensitiveAction(String action) {
        if (action == null) return false;
        return List.of(
                "DELETE_USER", "RESET_PASSWORD", "GRANT_PERMISSION", "REVOKE_PERMISSION",
                "UPDATE_ROLE_PERMISSION", "DELETE_ORDER", "DELETE_CUSTOMER"
        ).contains(action.toUpperCase());
    }

    private Specification<AuditLogEntity> buildSpec(
            String action, String entity, Integer userId, LocalDateTime from, LocalDateTime to
    ) {
        return (root, query, cb) -> {
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
    }

    private AuditLogResponse mapToResponse(AuditLogEntity entity) {
        Integer userId = null;
        String username = null;
        if (entity.getUser() != null) {
            userId = entity.getUser().getId();
            username = entity.getUser().getUsername();
        }
        return new AuditLogResponse(
                entity.getId(), userId, username, entity.getAction(),
                entity.getEntity(), entity.getEntityId(), entity.getCreatedAt(), entity.getDetail()
        );
    }

    private LoginAttemptResponse mapLoginResponse(LoginAttemptEntity entity) {
        Integer userId = entity.getUser() != null ? entity.getUser().getId() : null;
        return new LoginAttemptResponse(
                entity.getId(), userId, entity.getUsername(), entity.getSuccess(),
                entity.getIpAddress(), entity.getUserAgent(), entity.getCreatedAt()
        );
    }
}