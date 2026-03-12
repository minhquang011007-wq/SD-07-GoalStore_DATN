package com.example.demo.loyalty.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "Loyalty_Point_History")
@Data
public class LoyaltyPointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "customer_id")
    private Integer customerId;

    private Integer points;

    private String type;

    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}