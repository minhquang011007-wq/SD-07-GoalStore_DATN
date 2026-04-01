package com.example.demo.customer.repository;

import com.example.demo.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    List<Customer> findByTenContainingIgnoreCaseAndIsDeletedFalse(String ten);

    List<Customer> findByLoaiKhachAndIsDeletedFalse(String loaiKhach);

    List<Customer> findByEmailAndIsDeletedFalse(String email);

    List<Customer> findBySdtAndIsDeletedFalse(String sdt);

    Optional<Customer> findFirstByEmailAndIsDeletedFalse(String email);

    Optional<Customer> findFirstBySdtAndIsDeletedFalse(String sdt);
}
