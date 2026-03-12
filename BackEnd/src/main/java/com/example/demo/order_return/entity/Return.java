package com.example.demo.order_return.entity;

import com.example.demo.order.entity.Order;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Returns")
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "return_date", nullable = false)
    private LocalDateTime returnDate;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "refund_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal refundTotal;

    @Column(name = "note", length = 500)
    private String note;

    public Return() {
    }

    public Integer getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getRefundTotal() {
        return refundTotal;
    }

    public void setRefundTotal(BigDecimal refundTotal) {
        this.refundTotal = refundTotal;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}