package com.example.demo.auth.controller;

import com.example.demo.audit.service.AuditLogService;
import com.example.demo.auth.config.JwtTokenProvider;
import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.LoginResponse;
import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.repository.URepository;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.auth.dto.ForgotPasswordRequest;
import com.example.demo.auth.dto.ResetPasswordRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login & Token Management")
public class AuthController {
    private final URepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditLogService auditLogService;
    private final Map<String, String> otpStorage = new HashMap<>();

    @Operation(summary = "Đăng nhập")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest req,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        UserEntity user = userRepository.findByEmail(req.getEmail()).orElse(null);

        if (user == null) {
            auditLogService.recordLoginAttempt(req.getEmail(), null, false, ip, userAgent);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong email or password");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            auditLogService.recordLoginAttempt(user.getEmail(), user.getId(), false, ip, userAgent);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong email or password");
        }

        auditLogService.recordLoginAttempt(user.getEmail(), user.getId(), true, ip, userAgent);

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());

        return ResponseEntity.ok(
                new LoginResponse(token, user.getEmail(), user.getRole())
        );
    }

    @Operation(summary = "Quên mật khẩu")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) {

        UserEntity user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Email không tồn tại"
            );
        }

        String otp = String.valueOf(
                (int)((Math.random() * 900000) + 100000)
        );

        otpStorage.put(request.getEmail(), otp);

        System.out.println(
                "OTP của " +
                        request.getEmail() +
                        " là: " +
                        otp
        );

        return ResponseEntity.ok("Đã gửi OTP");
    }

    @Operation(summary = "Đổi mật khẩu")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {

        String savedOtp = otpStorage.get(request.getEmail());

        if (
                savedOtp == null ||
                        !savedOtp.equals(request.getOtp())
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "OTP không đúng"
            );
        }

        UserEntity user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User không tồn tại"
                        )
                );

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        otpStorage.remove(request.getEmail());

        return ResponseEntity.ok(
                "Đổi mật khẩu thành công"
        );
    }
}