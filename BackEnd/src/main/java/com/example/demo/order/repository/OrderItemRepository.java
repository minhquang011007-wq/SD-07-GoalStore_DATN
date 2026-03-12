package com.example.demo.order.repository;

import com.example.demo.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem,Integer> {

    List<OrderItem> findByOrderId(Integer orderId);

}