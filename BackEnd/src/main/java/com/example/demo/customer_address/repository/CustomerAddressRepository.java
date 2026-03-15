package com.example.demo.customer_address.repository;

import com.example.demo.customer_address.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Integer> {
    List<CustomerAddress> findByCustomerIdOrderByIdDesc(Integer customerId);

    Optional<CustomerAddress> findFirstByCustomerIdAndIsDefaultTrueOrderByIdDesc(Integer customerId);

    Optional<CustomerAddress> findByIdAndCustomerId(Integer id, Integer customerId);

    @Modifying
    @Query("update CustomerAddress a set a.isDefault = false where a.customer.id = :customerId and (:excludeId is null or a.id <> :excludeId)")
    void resetDefaultByCustomerId(@Param("customerId") Integer customerId, @Param("excludeId") Integer excludeId);
}
