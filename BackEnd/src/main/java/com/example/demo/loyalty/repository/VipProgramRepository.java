package com.example.demo.loyalty.repository;

import com.example.demo.loyalty.entity.VipProgram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VipProgramRepository extends JpaRepository<VipProgram,Integer> {
}