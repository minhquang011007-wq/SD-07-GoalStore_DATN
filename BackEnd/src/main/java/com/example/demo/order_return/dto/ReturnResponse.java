package com.example.demo.order_return.dto;

<<<<<<< HEAD
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReturnResponse {
    private Integer id;
    private Integer orderId;
    private LocalDateTime returnDate;
    private String reason;
    private BigDecimal refundTotal;
    private String note;

    public ReturnResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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
=======
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ReturnResponse {
    private Integer id;
    private Integer orderId;
    private String reason;
    private String note;
    private BigDecimal refundTotal;
    private LocalDateTime returnDate;
>>>>>>> 1ee6927b872139f2bca9bc0c573f017c0986ebc4
}