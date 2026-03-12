package com.example.demo.audit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SensitiveAlertMailServiceImpl implements SensitiveAlertMailService {
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Override
    public void sendSensitiveAlert(String subject, String content) {
        if (mailSender == null) return;

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("admin@gmail.com");
        msg.setSubject(subject);
        msg.setText(content);
        mailSender.send(msg);
    }
}