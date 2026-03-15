package com.example.demo.customer_auth.controller;

import com.example.demo.customer_auth.dto.CustomerAuthResponse;
import com.example.demo.customer_auth.dto.CustomerLoginRequest;
import com.example.demo.customer_auth.dto.CustomerRegisterRequest;
import com.example.demo.customer_auth.service.CustomerAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer-auth")
public class CustomerAuthController {

    private final CustomerAuthService customerAuthService;

    public CustomerAuthController(CustomerAuthService customerAuthService) {
        this.customerAuthService = customerAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerAuthResponse> register(@RequestBody CustomerRegisterRequest request) {
        return ResponseEntity.ok(customerAuthService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerAuthResponse> login(@RequestBody CustomerLoginRequest request) {
        return ResponseEntity.ok(customerAuthService.login(request));
    }
}
