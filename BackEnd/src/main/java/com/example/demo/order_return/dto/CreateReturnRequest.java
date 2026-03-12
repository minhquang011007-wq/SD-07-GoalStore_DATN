package com.example.demo.order_return.dto;

<<<<<<< HEAD
public class CreateReturnRequest {
    private Integer orderId;
    private String reason;
    private String note;

    public CreateReturnRequest() {
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
=======
import lombok.Data;

@Data
public class CreateReturnRequest {
    private String reason;
    private String note;
>>>>>>> 1ee6927b872139f2bca9bc0c573f017c0986ebc4
}