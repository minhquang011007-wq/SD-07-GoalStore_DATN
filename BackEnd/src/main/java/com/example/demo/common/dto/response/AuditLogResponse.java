package com.example.demo.common.dto.response;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private Long id;
    private Integer userId;
    private String username;
    private String action;
    private String entity;
    private Integer entityId;
    private LocalDateTime createdAt;
    private String detail;

    public AuditLogResponse() {
    }

    public AuditLogResponse(Long id, Integer userId, String username, String action,
                            String entity, Integer entityId, LocalDateTime createdAt, String detail) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.entity = entity;
        this.entityId = entityId;
        this.createdAt = createdAt;
        this.detail = detail;
    }

    public Long getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getAction() {
        return action;
    }

    public String getEntity() {
        return entity;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getDetail() {
        return detail;
    }
}