package com.example.demo.customer.dto;

import lombok.Data;

@Data
public class CustomerFilterRequest {

    private String keyword;
    private String loaiKhach;

    private Integer activeDays;

}