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

    List<Order> findByCustomerIdOrderByOrderDateDesc(Integer customerId);

    @Query("""
        select o.customer.id, coalesce(sum(o.total),0)
        from Order o
        where o.status <> 'HUY'
        group by o.customer.id
        order by coalesce(sum(o.total),0) desc
    """)
    List<Object[]> getCustomerSpending();

    @Query("""
        select c.id, c.ten, c.email, c.sdt, max(o.orderDate)
        from Order o
        join o.customer c
        group by c.id, c.ten, c.email, c.sdt
    """)
    List<Object[]> getLastOrderDateByCustomer();

    List<Order> findByOrderDateBefore(LocalDateTime dateTime);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId")
    Integer countOrders(Integer customerId);

    @Query("SELECT COALESCE(SUM(o.total),0) FROM Order o WHERE o.customer.id = :customerId")
    BigDecimal sumSpending(Integer customerId);

}