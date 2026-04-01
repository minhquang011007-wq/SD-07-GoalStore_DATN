package com.example.demo.loyalty.repository;

import com.example.demo.loyalty.entity.BirthdayNotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BirthdayNotificationLogRepository extends JpaRepository<BirthdayNotificationLog, Integer> {

    List<BirthdayNotificationLog> findByCustomerId(Integer customerId);

    boolean existsByCustomerIdAndChannelAndSentDateBetween(
            Integer customerId,
            String channel,
            java.time.LocalDateTime start,
            java.time.LocalDateTime end
    );

    @Query(value = """
        SELECT *
        FROM Birthday_Notification_Log b
        ORDER BY b.sent_date DESC
    """, nativeQuery = true)
    List<BirthdayNotificationLog> findAllOrderBySentDateDesc();
}