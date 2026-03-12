package com.example.demo.order_return.repository;

import com.example.demo.order_return.entity.Return;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReturnRepository extends JpaRepository<Return, Integer> {
    Optional<Return> findByOrderId(Integer orderId);
    boolean existsByOrderId(Integer orderId);
}