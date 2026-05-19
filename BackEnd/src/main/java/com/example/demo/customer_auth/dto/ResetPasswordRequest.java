package com.example.demo.customer_auth.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String token;

    private String newPassword;
}