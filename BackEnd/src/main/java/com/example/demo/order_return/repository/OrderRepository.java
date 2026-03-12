package com.example.demo.order_return.repository;

import com.example.demo.order_return.entity.Order;
import com.example.demo.order_return.entity.OrderChannel;
import com.example.demo.order_return.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByCustomerId(Integer customerId);

    List<Order> findByChannel(OrderChannel channel);

    List<Order> findByOrderDateBetween(LocalDateTime from, LocalDateTime to);

    List<Order> findByCustomerIdOrderByOrderDateDesc(Integer customerId);

    @Query("""
        select o.customer.id, coalesce(sum(o.total), 0)
        from Order o
        where o.status <> 'HUY'
        group by o.customer.id
        order by coalesce(sum(o.total), 0) desc
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
}