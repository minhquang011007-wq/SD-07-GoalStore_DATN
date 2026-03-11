package com.example.demo.order_return.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Audit_Log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "action")
    private String action;

    @Column(name = "entity")
    private String entity;

    @Column(name = "entity_id")
    private Integer entityId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "detail")
    private String detail;
}