package com.example.demo.loyalty.entity;

import com.example.demo.customer.entity.Customer;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Birthday_Notification_Log")
public class BirthdayNotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "sent_date", nullable = false)
    private LocalDateTime sentDate;

    @Column(name = "channel", nullable = false, length = 20)
    private String channel;

    @Column(name = "note", length = 255)
    private String note;

    @PrePersist
    public void prePersist() {
        if (sentDate == null) sentDate = LocalDateTime.now();
        if (channel == null) channel = "EMAIL";
    }

    public BirthdayNotificationLog() {
    }

    public Integer getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public String getChannel() {
        return channel;
    }

    public String getNote() {
        return note;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setNote(String note) {
        this.note = note;
    }
}