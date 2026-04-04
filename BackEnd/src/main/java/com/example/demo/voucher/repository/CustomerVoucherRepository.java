package com.example.demo.voucher.repository;

import com.example.demo.voucher.entity.CustomerVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Integer> {

    @Query("""
        SELECT cv
        FROM CustomerVoucher cv
        JOIN FETCH cv.voucher v
        WHERE cv.customer.id = :customerId
    """)
    List<CustomerVoucher> findByCustomerIdWithVoucher(Integer customerId);

    @Query("""
        SELECT cv
        FROM CustomerVoucher cv
        JOIN FETCH cv.voucher v
        WHERE cv.customer.id = :customerId
          AND cv.voucher.id = :voucherId
    """)
    Optional<CustomerVoucher> findByCustomerAndVoucher(Integer customerId, Integer voucherId);

    @Query("""
        SELECT COUNT(cv)
        FROM CustomerVoucher cv
        WHERE cv.voucher.id = :voucherId
    """)
    long countClaimedByVoucherId(Integer voucherId);

    @Query("""
        SELECT COUNT(cv)
        FROM CustomerVoucher cv
        WHERE cv.voucher.id = :voucherId
          AND cv.usedAt IS NOT NULL
    """)
    long countUsedByVoucherId(Integer voucherId);
}
