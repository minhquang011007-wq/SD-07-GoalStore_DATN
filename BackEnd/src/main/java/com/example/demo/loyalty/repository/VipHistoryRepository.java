package com.example.demo.loyalty.repository;

import com.example.demo.loyalty.entity.VipHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VipHistoryRepository extends JpaRepository<VipHistory,Integer> {

    List<VipHistory> findByCustomerIdOrderByChangedAtDesc(Integer customerId);

}