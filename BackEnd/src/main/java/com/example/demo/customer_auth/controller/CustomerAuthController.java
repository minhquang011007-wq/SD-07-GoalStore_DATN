package com.example.demo.customer_auth.controller;

import com.example.demo.customer_auth.dto.*;
import com.example.demo.customer_auth.service.CustomerAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

import java.util.UUID;

@RestController
@RequestMapping("/api/customer-auth")
public class CustomerAuthController {

    private final CustomerAuthService customerAuthService;

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender mailSender;

    private final Map<String, String> resetTokens
            = new HashMap<>();

    public CustomerAuthController(
            CustomerAuthService customerAuthService,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender
    ) {
        this.customerAuthService = customerAuthService;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerAuthResponse> register(@RequestBody CustomerRegisterRequest request) {
        return ResponseEntity.ok(customerAuthService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerAuthResponse> login(@RequestBody CustomerLoginRequest request) {
        return ResponseEntity.ok(customerAuthService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) {

        Customer customer = customerRepository
                .findFirstByEmailAndIsDeletedFalse(
                        request.getEmail()
                )
                .orElse(null);

        if (customer == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Email không tồn tại");
        }

        String token = UUID.randomUUID().toString();

        resetTokens.put(
                token,
                request.getEmail()
        );

        String resetLink =
                "http://localhost:5173/reset-password?token="
                        + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(request.getEmail());

        message.setSubject(
                "Đặt lại mật khẩu"
        );

        message.setText(
                "Nhấn vào link để đổi mật khẩu:\n"
                        + resetLink
        );

        mailSender.send(message);

        return ResponseEntity.ok(
                "Đã gửi email reset mật khẩu"
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {

        String email = resetTokens.get(
                request.getToken()
        );

        if (email == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Link không hợp lệ");
        }

        Customer customer = customerRepository
                .findFirstByEmailAndIsDeletedFalse(
                        email
                )
                .orElse(null);

        if (customer == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Customer không tồn tại");
        }

        customer.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        customerRepository.save(customer);

        resetTokens.remove(
                request.getToken()
        );

        return ResponseEntity.ok(
                "Đổi mật khẩu thành công"
        );
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request
    ) {

        String email = authentication.getName();

        Customer customer = customerRepository
                .findFirstByEmailAndIsDeletedFalse(email)
                .orElse(null);

        if (customer == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Không tìm thấy tài khoản");
        }

        boolean isCorrectOldPassword =
                passwordEncoder.matches(
                        request.getOldPassword(),
                        customer.getPassword()
                );

        if (!isCorrectOldPassword) {

            return ResponseEntity
                    .badRequest()
                    .body("Mật khẩu cũ không đúng");
        }

        if (
                !request.getNewPassword()
                        .equals(request.getConfirmPassword())
        ) {

            return ResponseEntity
                    .badRequest()
                    .body("Xác nhận mật khẩu không khớp");
        }

        customer.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        customerRepository.save(customer);

        return ResponseEntity.ok(
                "Đổi mật khẩu thành công"
        );
    }
}
