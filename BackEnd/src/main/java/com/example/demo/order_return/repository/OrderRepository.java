package com.example.demo.order_return.repository;

import com.example.demo.order_return.entity.Order;
import com.example.demo.order_return.entity.OrderChannel;
import com.example.demo.order_return.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerId(Integer customerId);
    List<Order> findByChannel(OrderChannel channel);
    List<Order> findByOrderDateBetween(LocalDateTime from, LocalDateTime to);
}