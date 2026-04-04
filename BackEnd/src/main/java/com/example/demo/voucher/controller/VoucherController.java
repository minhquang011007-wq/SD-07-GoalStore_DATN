package com.example.demo.voucher.controller;

import com.example.demo.voucher.dto.ClaimVoucherRequest;
import com.example.demo.voucher.dto.CustomerVoucherResponse;
import com.example.demo.voucher.dto.VoucherRequest;
import com.example.demo.voucher.dto.VoucherResponse;
import com.example.demo.voucher.service.VoucherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VoucherResponse>> getAdminVouchers() {
        return ResponseEntity.ok(voucherService.getAdminVouchers());
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VoucherResponse> createVoucher(@RequestBody VoucherRequest request) {
        return ResponseEntity.ok(voucherService.createVoucher(request));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VoucherResponse> updateVoucher(
            @PathVariable Integer id,
            @RequestBody VoucherRequest request
    ) {
        return ResponseEntity.ok(voucherService.updateVoucher(id, request));
    }

    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VoucherResponse> updateVoucherStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> payload
    ) {
        Boolean isActive = payload == null ? null : payload.get("isActive");
        return ResponseEntity.ok(voucherService.updateVoucherStatus(id, isActive));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Integer id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/wallet")
    public ResponseEntity<List<CustomerVoucherResponse>> getCustomerWallet(@RequestParam Integer customerId) {
        return ResponseEntity.ok(voucherService.getCustomerWallet(customerId));
    }

    @PostMapping("/customer/claim")
    public ResponseEntity<CustomerVoucherResponse> claimVoucher(@RequestBody ClaimVoucherRequest request) {
        return ResponseEntity.ok(voucherService.claimVoucher(request));
    }
}
