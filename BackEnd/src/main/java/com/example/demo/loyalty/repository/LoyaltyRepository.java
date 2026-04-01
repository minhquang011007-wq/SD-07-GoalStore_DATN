package com.example.demo.loyalty.repository;

import com.example.demo.loyalty.entity.LoyaltyPointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoyaltyRepository extends JpaRepository<LoyaltyPointHistory, Integer> {

    List<LoyaltyPointHistory> findByCustomerId(Integer customerId);
}