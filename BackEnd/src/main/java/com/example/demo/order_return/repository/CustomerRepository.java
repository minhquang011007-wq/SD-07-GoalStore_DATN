package com.example.demo.order_return.repository;

import com.example.demo.order_return.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}