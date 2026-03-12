package com.example.demo.audit.service;

import com.example.demo.audit.dto.response.SecurityAlertResponse;
import com.example.demo.audit.entity.SecurityAlertEntity;
import com.example.demo.audit.repository.SecurityAlertRepository;
import com.example.demo.user.exception.ResourceNotFoundException;
import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.repository.URepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class SecurityAlertServiceImpl implements SecurityAlertService {
    @Autowired
    private SecurityAlertRepository securityAlertRepository;

    @Autowired
    private URepository userRepository;

    @Override
    public void createAlert(Integer userId, String alertType, String severity, String message, String ipAddress) {
        SecurityAlertEntity entity = new SecurityAlertEntity();
        if (userId != null) {
            UserEntity user = userRepository.findById(userId).orElse(null);
            entity.setUser(user);
        }
        entity.setAlertType(alertType);
        entity.setSeverity(severity);
        entity.setMessage(message);
        entity.setIpAddress(ipAddress);
        entity.setResolved(false);
        securityAlertRepository.save(entity);
    }

    @Override
    public Page<SecurityAlertResponse> getAlerts(Boolean resolved, String alertType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<SecurityAlertEntity> data;

        if (resolved != null && alertType != null && !alertType.isBlank()) {
            data = securityAlertRepository.findByResolvedAndAlertTypeContainingIgnoreCase(resolved, alertType, pageable);
        } else if (resolved != null) {
            data = securityAlertRepository.findByResolved(resolved, pageable);
        } else if (alertType != null && !alertType.isBlank()) {
            data = securityAlertRepository.findByAlertTypeContainingIgnoreCase(alertType, pageable);
        } else {
            data = securityAlertRepository.findAll(pageable);
        }

        return data.map(this::mapToResponse);
    }

    @Override
    public SecurityAlertResponse resolveAlert(Long id) {
        SecurityAlertEntity entity = securityAlertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy alert với id = " + id));
        entity.setResolved(true);
        return mapToResponse(securityAlertRepository.save(entity));
    }

    private SecurityAlertResponse mapToResponse(SecurityAlertEntity entity) {
        Integer userId = entity.getUser() != null ? entity.getUser().getId() : null;
        String username = entity.getUser() != null ? entity.getUser().getUsername() : null;
        return new SecurityAlertResponse(
                entity.getId(),
                userId,
                username,
                entity.getAlertType(),
                entity.getSeverity(),
                entity.getMessage(),
                entity.getIpAddress(),
                entity.getCreatedAt(),
                entity.getResolved()
        );
    }
}