package com.example.demo.loyalty.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "Birthday_Notification_Log")
@Data
public class BirthdayNotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    private String channel;

    private String note;

}