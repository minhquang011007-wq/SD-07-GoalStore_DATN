package com.example.demo.order.repository;

import com.example.demo.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("""
        SELECT o
        FROM Order o
        JOIN FETCH o.customer c
        ORDER BY o.orderDate DESC
    """)
    List<Order> findAllWithCustomerOrderByOrderDateDesc();

    @Query("""
        SELECT o
        FROM Order o
        JOIN FETCH o.customer c
        WHERE o.status = :status
        ORDER BY o.orderDate DESC
    """)
    List<Order> findByStatusWithCustomerOrderByOrderDateDesc(String status);

    @Query("""
        SELECT o
        FROM Order o
        JOIN FETCH o.customer c
        WHERE c.id = :customerId
        ORDER BY o.orderDate DESC
    """)
    List<Order> findByCustomerIdOrderByOrderDateDesc(Integer customerId);

    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.customer.id = :customerId
          AND o.status <> 'HUY'
    """)
    Integer countValidOrders(Integer customerId);

    @Query("""
        SELECT COALESCE(SUM(o.total), 0)
        FROM Order o
        WHERE o.customer.id = :customerId
          AND o.status <> 'HUY'
    """)
    BigDecimal sumValidSpending(Integer customerId);

    @Query("""
        SELECT MAX(o.orderDate)
        FROM Order o
        WHERE o.customer.id = :customerId
          AND o.status <> 'HUY'
    """)
    LocalDateTime findLastOrderDateByCustomerId(Integer customerId);

    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.customer.id = :customerId
          AND o.status <> 'HUY'
          AND o.orderDate >= :fromDate
    """)
    Integer countOrdersFromDate(Integer customerId, LocalDateTime fromDate);

    @Query("""
        SELECT COALESCE(SUM(o.total), 0)
        FROM Order o
        WHERE o.customer.id = :customerId
          AND o.status <> 'HUY'
          AND o.orderDate >= :fromDate
    """)
    BigDecimal sumSpendingFromDate(Integer customerId, LocalDateTime fromDate);

    @Query("""
        SELECT COALESCE(SUM(o.total), 0)
        FROM Order o
        WHERE o.status = 'HOAN_TAT'
    """)
    BigDecimal getCompletedRevenue();
}