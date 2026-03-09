package com.example.demo.customer.repository;

import com.example.demo.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    List<Customer> findByTenContainingIgnoreCase(String ten);

    List<Customer> findByLoaiKhach(String loaiKhach);

    List<Customer> findByEmail(String email);

    List<Customer> findBySdt(String sdt);
}