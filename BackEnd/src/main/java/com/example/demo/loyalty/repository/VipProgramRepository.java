package com.example.demo.loyalty.repository;

import com.example.demo.loyalty.entity.VipProgram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VipProgramRepository extends JpaRepository<VipProgram, Integer> {

    List<VipProgram> findByIsActiveTrueOrderByMinPointsAsc();

    boolean existsByLevelNameIgnoreCase(String levelName);
}