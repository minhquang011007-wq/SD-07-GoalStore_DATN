package com.example.demo.audit.service;

public interface SensitiveAlertMailService {
    void sendSensitiveAlert(String subject, String content);
}