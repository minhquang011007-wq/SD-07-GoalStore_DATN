package com.example.demo.voucher.service.impl;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.voucher.dto.ClaimVoucherRequest;
import com.example.demo.voucher.dto.CustomerVoucherResponse;
import com.example.demo.voucher.dto.VoucherRequest;
import com.example.demo.voucher.dto.VoucherResponse;
import com.example.demo.voucher.entity.CustomerVoucher;
import com.example.demo.voucher.entity.Voucher;
import com.example.demo.voucher.repository.CustomerVoucherRepository;
import com.example.demo.voucher.repository.VoucherRepository;
import com.example.demo.voucher.service.VoucherService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final CustomerVoucherRepository customerVoucherRepository;
    private final CustomerRepository customerRepository;

    public VoucherServiceImpl(
            VoucherRepository voucherRepository,
            CustomerVoucherRepository customerVoucherRepository,
            CustomerRepository customerRepository
    ) {
        this.voucherRepository = voucherRepository;
        this.customerVoucherRepository = customerVoucherRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<VoucherResponse> getAdminVouchers() {
        return voucherRepository.findByIsDeletedFalseOrderByIdDesc()
                .stream()
                .map(this::toAdminResponse)
                .toList();
    }

    @Override
    public VoucherResponse createVoucher(VoucherRequest request) {
        validateVoucherRequest(request, null);

        Voucher voucher = new Voucher();
        applyVoucherData(voucher, request);
        voucher = voucherRepository.save(voucher);
        return toAdminResponse(voucher);
    }

    @Override
    public VoucherResponse updateVoucher(Integer id, VoucherRequest request) {
        Voucher voucher = voucherRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));

        validateVoucherRequest(request, id);
        applyVoucherData(voucher, request);
        voucher = voucherRepository.save(voucher);
        return toAdminResponse(voucher);
    }

    @Override
    public VoucherResponse updateVoucherStatus(Integer id, Boolean isActive) {
        Voucher voucher = voucherRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));
        voucher.setIsActive(Boolean.TRUE.equals(isActive));
        voucher = voucherRepository.save(voucher);
        return toAdminResponse(voucher);
    }

    @Override
    public void deleteVoucher(Integer id) {
        Voucher voucher = voucherRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));
        voucher.setIsDeleted(true);
        voucher.setIsActive(false);
        voucherRepository.save(voucher);
    }

    @Override
    public List<CustomerVoucherResponse> getCustomerWallet(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        List<Voucher> activeVouchers = voucherRepository.findByIsDeletedFalseAndIsActiveTrueOrderByIdDesc();
        List<CustomerVoucher> claimedVouchers = customerVoucherRepository.findByCustomerIdWithVoucher(customer.getId());

        Map<Integer, CustomerVoucher> claimedMap = new HashMap<>();
        for (CustomerVoucher claimed : claimedVouchers) {
            claimedMap.put(claimed.getVoucher().getId(), claimed);
        }

        return activeVouchers.stream()
                .sorted(Comparator.comparing(Voucher::getId).reversed())
                .map(voucher -> toCustomerResponse(voucher, claimedMap.get(voucher.getId())))
                .toList();
    }

    @Override
    public CustomerVoucherResponse claimVoucher(ClaimVoucherRequest request) {
        if (request == null || request.getCustomerId() == null) {
            throw new RuntimeException("Thiếu customerId");
        }
        if (request.getVoucherId() == null) {
            throw new RuntimeException("Thiếu voucherId");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        Voucher voucher = getActiveVoucher(request.getVoucherId());

        customerVoucherRepository.findByCustomerAndVoucher(customer.getId(), voucher.getId())
                .ifPresent(item -> {
                    throw new RuntimeException("Bạn đã nhận voucher này rồi");
                });

        int remainingQuantity = calculateRemainingQuantity(voucher);
        if (remainingQuantity <= 0) {
            throw new RuntimeException("Voucher này đã hết lượt nhận");
        }

        CustomerVoucher customerVoucher = new CustomerVoucher();
        customerVoucher.setCustomer(customer);
        customerVoucher.setVoucher(voucher);
        customerVoucher.setClaimedAt(LocalDateTime.now());
        customerVoucher = customerVoucherRepository.save(customerVoucher);

        return toCustomerResponse(voucher, customerVoucher);
    }

    @Override
    public Voucher getActiveVoucher(Integer voucherId) {
        Voucher voucher = voucherRepository.findByIdAndIsDeletedFalse(voucherId)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
        if (!Boolean.TRUE.equals(voucher.getIsActive())) {
            throw new RuntimeException("Voucher hiện không hoạt động");
        }
        return voucher;
    }

    @Override
    public CustomerVoucher getUsableCustomerVoucher(Integer customerId, Integer voucherId) {
        getActiveVoucher(voucherId);

        CustomerVoucher customerVoucher = customerVoucherRepository.findByCustomerAndVoucher(customerId, voucherId)
                .orElseThrow(() -> new RuntimeException("Khách hàng chưa nhận voucher này"));

        if (customerVoucher.getUsedAt() != null) {
            throw new RuntimeException("Voucher này đã được sử dụng rồi");
        }

        return customerVoucher;
    }

    @Override
    public BigDecimal calculateDiscountAmount(Voucher voucher, BigDecimal subtotal) {
        if (voucher == null || subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal minOrderAmount = voucher.getMinOrderAmount() == null ? BigDecimal.ZERO : voucher.getMinOrderAmount();
        if (subtotal.compareTo(minOrderAmount) < 0) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu để dùng voucher");
        }

        BigDecimal percent = voucher.getDiscountPercent() == null ? BigDecimal.ZERO : voucher.getDiscountPercent();
        if (percent.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return subtotal.multiply(percent)
                .divide(new BigDecimal("100"), 0, RoundingMode.DOWN);
    }

    @Override
    public void markVoucherUsed(CustomerVoucher customerVoucher, Integer orderId, String orderCode) {
        if (customerVoucher == null) {
            return;
        }
        if (customerVoucher.getUsedAt() != null) {
            return;
        }

        customerVoucher.setUsedAt(LocalDateTime.now());
        customerVoucher.setUsedOrderId(orderId);
        customerVoucher.setUsedOrderCode(orderCode);
        customerVoucherRepository.save(customerVoucher);
    }

    private void validateVoucherRequest(VoucherRequest request, Integer editingId) {
        if (request == null) {
            throw new RuntimeException("Dữ liệu voucher không hợp lệ");
        }
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new RuntimeException("Mã voucher không được để trống");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Tên voucher không được để trống");
        }
        if (request.getDiscountPercent() == null) {
            throw new RuntimeException("Phần trăm giảm giá không được để trống");
        }
        if (request.getDiscountPercent().compareTo(BigDecimal.ZERO) <= 0 || request.getDiscountPercent().compareTo(new BigDecimal("100")) > 0) {
            throw new RuntimeException("Phần trăm giảm giá phải lớn hơn 0 và nhỏ hơn hoặc bằng 100");
        }
        if (request.getMinOrderAmount() != null && request.getMinOrderAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá trị đơn tối thiểu không hợp lệ");
        }
        if (request.getTotalQuantity() == null || request.getTotalQuantity() <= 0) {
            throw new RuntimeException("Số lượng voucher phải lớn hơn 0");
        }

        String normalizedCode = request.getCode().trim().toUpperCase();
        voucherRepository.findFirstByCodeIgnoreCaseAndIsDeletedFalse(normalizedCode)
                .ifPresent(existing -> {
                    if (editingId == null || !existing.getId().equals(editingId)) {
                        throw new RuntimeException("Mã voucher đã tồn tại");
                    }
                });

        if (editingId != null) {
            Voucher currentVoucher = voucherRepository.findByIdAndIsDeletedFalse(editingId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));
            int claimedCount = getClaimedCount(currentVoucher.getId());
            if (request.getTotalQuantity() < claimedCount) {
                throw new RuntimeException("Số lượng tổng không được nhỏ hơn số voucher đã nhận");
            }
        }
    }

    private void applyVoucherData(Voucher voucher, VoucherRequest request) {
        voucher.setCode(request.getCode().trim().toUpperCase());
        voucher.setName(request.getName().trim());
        voucher.setDescription(request.getDescription() == null ? null : request.getDescription().trim());
        voucher.setDiscountPercent(request.getDiscountPercent());
        voucher.setMinOrderAmount(request.getMinOrderAmount() == null ? BigDecimal.ZERO : request.getMinOrderAmount());
        voucher.setTotalQuantity(request.getTotalQuantity());
        voucher.setIsActive(request.getIsActive() == null ? true : request.getIsActive());
        voucher.setIsDeleted(false);
    }

    private VoucherResponse toAdminResponse(Voucher voucher) {
        int claimedCount = getClaimedCount(voucher.getId());
        int usedCount = getUsedCount(voucher.getId());
        int remainingQuantity = Math.max(0, (voucher.getTotalQuantity() == null ? 0 : voucher.getTotalQuantity()) - claimedCount);

        VoucherResponse response = new VoucherResponse();
        response.setId(voucher.getId());
        response.setCode(voucher.getCode());
        response.setName(voucher.getName());
        response.setDescription(voucher.getDescription());
        response.setDiscountPercent(voucher.getDiscountPercent());
        response.setMinOrderAmount(voucher.getMinOrderAmount());
        response.setTotalQuantity(voucher.getTotalQuantity());
        response.setRemainingQuantity(remainingQuantity);
        response.setIsActive(voucher.getIsActive());
        response.setIsDeleted(voucher.getIsDeleted());
        response.setCreatedAt(voucher.getCreatedAt());
        response.setUpdatedAt(voucher.getUpdatedAt());
        response.setClaimedCount(claimedCount);
        response.setUsedCount(usedCount);
        return response;
    }

    private CustomerVoucherResponse toCustomerResponse(Voucher voucher, CustomerVoucher customerVoucher) {
        CustomerVoucherResponse response = new CustomerVoucherResponse();
        response.setId(voucher.getId());
        response.setCode(voucher.getCode());
        response.setName(voucher.getName());
        response.setDescription(voucher.getDescription());
        response.setDiscountPercent(voucher.getDiscountPercent());
        response.setMinOrderAmount(voucher.getMinOrderAmount());
        response.setTotalQuantity(voucher.getTotalQuantity());
        response.setRemainingQuantity(calculateRemainingQuantity(voucher));
        response.setIsActive(voucher.getIsActive());
        response.setClaimed(customerVoucher != null);
        response.setUsed(customerVoucher != null && customerVoucher.getUsedAt() != null);
        response.setClaimedAt(customerVoucher != null ? customerVoucher.getClaimedAt() : null);
        response.setUsedAt(customerVoucher != null ? customerVoucher.getUsedAt() : null);
        response.setUsedOrderId(customerVoucher != null ? customerVoucher.getUsedOrderId() : null);
        response.setUsedOrderCode(customerVoucher != null ? customerVoucher.getUsedOrderCode() : null);
        return response;
    }

    private int getClaimedCount(Integer voucherId) {
        return (int) customerVoucherRepository.countClaimedByVoucherId(voucherId);
    }

    private int getUsedCount(Integer voucherId) {
        return (int) customerVoucherRepository.countUsedByVoucherId(voucherId);
    }

    private int calculateRemainingQuantity(Voucher voucher) {
        int totalQuantity = voucher.getTotalQuantity() == null ? 0 : voucher.getTotalQuantity();
        return Math.max(0, totalQuantity - getClaimedCount(voucher.getId()));
    }
}
