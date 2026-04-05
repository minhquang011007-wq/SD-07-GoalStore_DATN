package com.example.demo.payment.controller;

import com.example.demo.payment.dto.VnpayCreatePaymentResponse;
import com.example.demo.payment.dto.VnpayIpnResponse;
import com.example.demo.payment.dto.VnpayProcessResult;
import com.example.demo.payment.service.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/api/public/payments/vnpay")
public class VnpayController {

    private final VnpayService vnpayService;

    @Value("${app.frontend-base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    public VnpayController(VnpayService vnpayService) {
        this.vnpayService = vnpayService;
    }

    @GetMapping("/{orderId}/payment-url")
    public ResponseEntity<VnpayCreatePaymentResponse> createPaymentUrl(
            @PathVariable Integer orderId,
            @RequestParam(defaultValue = "false") boolean qrOnly,
            HttpServletRequest request
    ) {
        String clientIp = extractClientIp(request);
        return ResponseEntity.ok(vnpayService.createPaymentUrl(orderId, clientIp, qrOnly));
    }

    @GetMapping("/ipn")
    public ResponseEntity<VnpayIpnResponse> handleIpn(@RequestParam Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return ResponseEntity.ok(new VnpayIpnResponse("99", "Invalid request"));
        }

        try {
            VnpayProcessResult result = vnpayService.processIpnCallback(params);

            if (!result.isValidSignature()) {
                return ResponseEntity.ok(new VnpayIpnResponse("97", "Invalid signature"));
            }
            if (!result.isOrderFound()) {
                return ResponseEntity.ok(new VnpayIpnResponse("01", "Order not found"));
            }
            if (!result.isAmountValid()) {
                return ResponseEntity.ok(new VnpayIpnResponse("04", "invalid amount"));
            }
            if (result.isAlreadyConfirmed()) {
                return ResponseEntity.ok(new VnpayIpnResponse("02", "Order already confirmed"));
            }
            return ResponseEntity.ok(new VnpayIpnResponse("00", "Confirm Success"));
        } catch (Exception e) {
            return ResponseEntity.ok(new VnpayIpnResponse("99", "Unknown error"));
        }
    }

    @GetMapping("/return")
    public ResponseEntity<Void> handleReturn(@RequestParam Map<String, String> params) {
        VnpayProcessResult result = vnpayService.processIpnCallback(params);

        String paymentStatus = !result.isValidSignature()
                ? "invalid"
                : result.isSuccessfulPayment() ? "success" : "failed";

        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendBaseUrl)
                .path("/payment/vnpay-return")
                .queryParam("status", paymentStatus)
                .queryParamIfPresent("orderId", java.util.Optional.ofNullable(result.getOrderId()))
                .queryParamIfPresent("txnRef", java.util.Optional.ofNullable(result.getTransactionRef()))
                .queryParamIfPresent("responseCode", java.util.Optional.ofNullable(result.getResponseCode()))
                .queryParamIfPresent("transactionStatus", java.util.Optional.ofNullable(result.getTransactionStatus()))
                .queryParam("message", result.getMessage())
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, redirectUrl);
        return ResponseEntity.status(302).headers(headers).build();
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded;
        }
        return request.getRemoteAddr();
    }
}