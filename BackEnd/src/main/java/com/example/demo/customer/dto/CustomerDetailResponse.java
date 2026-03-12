package com.example.demo.customer.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CustomerDetailResponse {

    private Integer id;
    private String ten;
    private String email;
    private String sdt;
    private String loaiKhach;

    private Integer diemTichLuy;

    private Integer totalOrders;
    private BigDecimal totalSpending;

    private String vipLevel;

    private List<CustomerOrderHistoryResponse> orders;
}