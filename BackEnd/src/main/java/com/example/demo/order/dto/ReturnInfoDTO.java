package com.example.demo.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReturnInfoDTO {
    private Integer id;
    private Integer orderId;
    private String reason;
    private String note;
    private BigDecimal refundTotal;
    private LocalDateTime returnDate;

    // getter setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public BigDecimal getRefundTotal() { return refundTotal; }
    public void setRefundTotal(BigDecimal refundTotal) { this.refundTotal = refundTotal; }

    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
}