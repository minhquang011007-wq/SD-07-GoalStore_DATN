package com.example.demo.customer_auth.service;

import com.example.demo.customer_auth.dto.CustomerAuthResponse;
import com.example.demo.customer_auth.dto.CustomerLoginRequest;
import com.example.demo.customer_auth.dto.CustomerRegisterRequest;

public interface CustomerAuthService {
    CustomerAuthResponse register(CustomerRegisterRequest request);
    CustomerAuthResponse login(CustomerLoginRequest request);
}
