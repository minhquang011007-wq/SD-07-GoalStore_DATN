package com.example.demo.loyalty.repository;

import com.example.demo.loyalty.entity.BirthdayNotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BirthdayNotificationLogRepository extends JpaRepository<BirthdayNotificationLog,Integer> {

    List<BirthdayNotificationLog> findByCustomerId(Integer customerId);

}