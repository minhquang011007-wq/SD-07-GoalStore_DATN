package com.example.demo.loyalty.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "Vip_History")
@Data
public class VipHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "old_level")
    private String oldLevel;

    @Column(name = "new_level")
    private String newLevel;

    private String reason;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

}