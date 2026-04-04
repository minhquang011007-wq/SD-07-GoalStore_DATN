package com.example.demo.voucher.service;

import com.example.demo.voucher.dto.ClaimVoucherRequest;
import com.example.demo.voucher.dto.CustomerVoucherResponse;
import com.example.demo.voucher.dto.VoucherRequest;
import com.example.demo.voucher.dto.VoucherResponse;
import com.example.demo.voucher.entity.CustomerVoucher;
import com.example.demo.voucher.entity.Voucher;

import java.math.BigDecimal;
import java.util.List;

public interface VoucherService {
    List<VoucherResponse> getAdminVouchers();
    VoucherResponse createVoucher(VoucherRequest request);
    VoucherResponse updateVoucher(Integer id, VoucherRequest request);
    VoucherResponse updateVoucherStatus(Integer id, Boolean isActive);
    void deleteVoucher(Integer id);

    List<CustomerVoucherResponse> getCustomerWallet(Integer customerId);
    CustomerVoucherResponse claimVoucher(ClaimVoucherRequest request);

    Voucher getActiveVoucher(Integer voucherId);
    CustomerVoucher getUsableCustomerVoucher(Integer customerId, Integer voucherId);
    BigDecimal calculateDiscountAmount(Voucher voucher, BigDecimal subtotal);
    void markVoucherUsed(CustomerVoucher customerVoucher, Integer orderId, String orderCode);
}
