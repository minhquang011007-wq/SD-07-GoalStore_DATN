package com.example.demo.audit.entity;

import com.example.demo.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "Security_Alerts")
@Getter
@Setter
public class SecurityAlertEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "alert_type", nullable = false, length = 50)
    private String alertType;

    @Column(name = "severity", nullable = false, length = 20)
    private String severity;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved", nullable = false)
    private Boolean resolved;
}