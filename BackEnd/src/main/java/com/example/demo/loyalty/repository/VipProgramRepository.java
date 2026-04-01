package com.example.demo.loyalty.repository;

import com.example.demo.loyalty.entity.VipProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VipProgramRepository extends JpaRepository<VipProgram, Integer> {

    List<VipProgram> findByIsActiveTrueOrderByMinPointsAsc();
}