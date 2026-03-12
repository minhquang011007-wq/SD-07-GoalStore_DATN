package com.example.demo.audit.service;

import com.example.demo.audit.dto.response.SecurityAlertResponse;
import org.springframework.data.domain.Page;

public interface SecurityAlertService {
    void createAlert(Integer userId, String alertType, String severity, String message, String ipAddress);

    Page<SecurityAlertResponse> getAlerts(Boolean resolved, String alertType, int page, int size);

    SecurityAlertResponse resolveAlert(Long id);
}