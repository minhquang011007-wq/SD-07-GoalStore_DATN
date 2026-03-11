package com.example.demo.loyalty.repository;

import com.example.demo.loyalty.entity.BirthdayNotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BirthdayNotificationLogRepository extends JpaRepository<BirthdayNotificationLog, Integer> {

    List<BirthdayNotificationLog> findByCustomerIdOrderBySentDateDesc(Integer customerId);
}