package com.example.demo.loyalty.repository;

import com.example.demo.loyalty.entity.LoyaltyPointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoyaltyRepository extends JpaRepository<LoyaltyPointHistory,Integer> {

    List<LoyaltyPointHistory> findByCustomerIdOrderByCreatedAtDesc(Integer customerId);

}