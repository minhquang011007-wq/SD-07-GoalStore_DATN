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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login & Token Management")
public class AuthController {
    private final URepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditLogService auditLogService;

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
}