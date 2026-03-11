package com.example.demo.loyalty.repository;

import com.example.demo.loyalty.entity.VipHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VipHistoryRepository extends JpaRepository<VipHistory, Integer> {

    List<VipHistory> findByCustomerIdOrderByChangedAtDesc(Integer customerId);
}