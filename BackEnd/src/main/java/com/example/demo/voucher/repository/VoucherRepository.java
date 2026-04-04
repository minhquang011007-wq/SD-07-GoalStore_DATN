package com.example.demo.voucher.repository;

import com.example.demo.voucher.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    List<Voucher> findByIsDeletedFalseOrderByIdDesc();
    List<Voucher> findByIsDeletedFalseAndIsActiveTrueOrderByIdDesc();
    Optional<Voucher> findByIdAndIsDeletedFalse(Integer id);
    Optional<Voucher> findFirstByCodeIgnoreCaseAndIsDeletedFalse(String code);
}
