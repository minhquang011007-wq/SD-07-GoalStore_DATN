package com.example.demo.voucher.entity;

import com.example.demo.customer.entity.Customer;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "Customer_Vouchers",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_customer_voucher", columnNames = {"customer_id", "voucher_id"})
        }
)
public class CustomerVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @Column(name = "claimed_at", nullable = false)
    private LocalDateTime claimedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "used_order_id")
    private Integer usedOrderId;

    @Column(name = "used_order_code", length = 50)
    private String usedOrderCode;

    @PrePersist
    public void prePersist() {
        if (claimedAt == null) {
            claimedAt = LocalDateTime.now();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public LocalDateTime getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(LocalDateTime claimedAt) {
        this.claimedAt = claimedAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public Integer getUsedOrderId() {
        return usedOrderId;
    }

    public void setUsedOrderId(Integer usedOrderId) {
        this.usedOrderId = usedOrderId;
    }

    public String getUsedOrderCode() {
        return usedOrderCode;
    }

    public void setUsedOrderCode(String usedOrderCode) {
        this.usedOrderCode = usedOrderCode;
    }
}
