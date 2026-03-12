package com.example.demo.audit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class LoginAttemptResponse {
    private Long id;
    private Integer userId;
    private String username;
    private Boolean success;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
}