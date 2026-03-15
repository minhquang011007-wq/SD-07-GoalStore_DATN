package com.example.demo.customer_auth.service.impl;

import com.example.demo.auth.config.JwtTokenProvider;
import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.customer_auth.dto.CustomerAuthResponse;
import com.example.demo.customer_auth.dto.CustomerLoginRequest;
import com.example.demo.customer_auth.dto.CustomerRegisterRequest;
import com.example.demo.customer_auth.service.CustomerAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerAuthServiceImpl implements CustomerAuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public CustomerAuthServiceImpl(CustomerRepository customerRepository,
                                   PasswordEncoder passwordEncoder,
                                   JwtTokenProvider jwtTokenProvider) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public CustomerAuthResponse register(CustomerRegisterRequest request) {
        validateRegisterRequest(request);

        customerRepository.findFirstByEmailAndIsDeletedFalse(request.getEmail())
                .ifPresent(c -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã tồn tại");
                });

        if (request.getSdt() != null && !request.getSdt().isBlank()) {
            customerRepository.findFirstBySdtAndIsDeletedFalse(request.getSdt())
                    .ifPresent(c -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại đã tồn tại");
                    });
        }

        Customer customer = new Customer();
        customer.setTen(request.getTen().trim());
        customer.setEmail(request.getEmail().trim().toLowerCase());
        customer.setSdt(normalizePhone(request.getSdt()));
        customer.setNgaySinh(request.getNgaySinh());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setStatus("ACTIVE");
        customer.setLoaiKhach("THUONG");
        customer.setDiemTichLuy(0);
        customer.setIsDeleted(false);

        Customer saved = customerRepository.save(customer);
        String token = jwtTokenProvider.generateToken(saved.getEmail(), "CUSTOMER");
        return new CustomerAuthResponse(token, saved.getId(), saved.getEmail(), saved.getTen(), "CUSTOMER");
    }

    @Override
    public CustomerAuthResponse login(CustomerLoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được để trống");
        }

        Customer customer = customerRepository.findFirstByEmailAndIsDeletedFalse(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai email hoặc mật khẩu"));

        if (!"ACTIVE".equalsIgnoreCase(customer.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản khách hàng không hoạt động");
        }
        if (customer.getPassword() == null || !passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai email hoặc mật khẩu");
        }

        String token = jwtTokenProvider.generateToken(customer.getEmail(), "CUSTOMER");
        return new CustomerAuthResponse(token, customer.getId(), customer.getEmail(), customer.getTen(), "CUSTOMER");
    }

    private void validateRegisterRequest(CustomerRegisterRequest request) {
        if (request.getTen() == null || request.getTen().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ tên không được để trống");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được để trống");
        }
    }

    private String normalizePhone(String phone) {
        if (phone == null) return null;
        String normalized = phone.trim();
        return normalized.isBlank() ? null : normalized;
    }
}
