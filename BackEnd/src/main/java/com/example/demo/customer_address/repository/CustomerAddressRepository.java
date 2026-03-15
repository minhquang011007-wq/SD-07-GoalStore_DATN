package com.example.demo.customer_address.repository;

import com.example.demo.customer_address.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Integer> {
    List<CustomerAddress> findByCustomerIdOrderByIdDesc(Integer customerId);
}
