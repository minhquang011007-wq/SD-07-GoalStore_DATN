package com.example.demo.loyalty.repository;

import com.example.demo.loyalty.entity.LoyaltyPointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LoyaltyRepository extends JpaRepository<LoyaltyPointHistory, Integer> {

    List<LoyaltyPointHistory> findByCustomerIdOrderByCreatedAtDesc(Integer customerId);

    @Query("""
        SELECT l
        FROM LoyaltyPointHistory l
        WHERE (:customerId IS NULL OR l.customerId = :customerId)
          AND (:type IS NULL OR upper(l.type) = upper(:type))
          AND (:fromDate IS NULL OR l.createdAt >= :fromDate)
          AND (:toDate IS NULL OR l.createdAt <= :toDate)
        ORDER BY l.createdAt DESC
    """)
    List<LoyaltyPointHistory> filterHistory(
            Integer customerId,
            String type,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );

    @Query("""
        SELECT COALESCE(SUM(l.points), 0)
        FROM LoyaltyPointHistory l
        WHERE l.customerId = :customerId
          AND upper(l.type) = 'ADD'
          AND l.note LIKE concat('%#', :orderId, '%')
    """)
    Integer getPointsAddedByOrder(Integer customerId, Integer orderId);

    @Query("""
        SELECT COALESCE(SUM(l.points), 0)
        FROM LoyaltyPointHistory l
        WHERE l.customerId = :customerId
          AND upper(l.type) IN ('SUBTRACT', 'REFUND')
          AND l.note LIKE concat('%#', :orderId, '%')
    """)
    Integer getPointsReducedByOrder(Integer customerId, Integer orderId);
}