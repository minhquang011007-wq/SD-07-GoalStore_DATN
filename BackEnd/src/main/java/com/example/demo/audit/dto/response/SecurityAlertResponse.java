package com.example.demo.audit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SecurityAlertResponse {
    private Long id;
    private Integer userId;
    private String username;
    private String alertType;
    private String severity;
    private String message;
    private String ipAddress;
    private LocalDateTime createdAt;
    private Boolean resolved;
}