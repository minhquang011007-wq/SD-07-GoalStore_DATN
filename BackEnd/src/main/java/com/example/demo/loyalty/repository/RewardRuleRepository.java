package com.example.demo.loyalty.repository;

import com.example.demo.loyalty.entity.RewardRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRuleRepository extends JpaRepository<RewardRule, Integer> {

    List<RewardRule> findByIsActiveTrueOrderByRequiredPointsAsc();

    boolean existsByRewardNameIgnoreCase(String rewardName);
}