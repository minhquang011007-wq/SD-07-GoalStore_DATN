package com.example.demo.payment.service;

import com.example.demo.order.dto.UpdateOrderPaymentStatusRequest;
import com.example.demo.order.entity.Order;
import com.example.demo.order.repository.OrderRepository;
import com.example.demo.order.service.OrderService;
import com.example.demo.payment.dto.VnpayCreatePaymentResponse;
import com.example.demo.payment.dto.VnpayProcessResult;
import com.example.demo.payment.util.VnpayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class VnpayService {

    private static final DateTimeFormatter VNPAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final ZoneId VNPAY_ZONE = ZoneId.of("Asia/Bangkok");

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Value("${vnpay.tmn-code:}")
    private String tmnCode;

    @Value("${vnpay.hash-secret:}")
    private String hashSecret;

    @Value("${vnpay.pay-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String payUrl;

    @Value("${vnpay.return-url:http://localhost:8080/api/public/payments/vnpay/return}")
    private String returnUrl;

    @Value("${vnpay.expire-minutes:15}")
    private long expireMinutes;

    public VnpayService(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    public VnpayCreatePaymentResponse createPaymentUrl(Integer orderId, String clientIp, boolean qrOnly) {
        validateConfig();

        Order order = findOrder(orderId);
        validateOrderForPayment(order);

        LocalDateTime now = LocalDateTime.now(VNPAY_ZONE);
        LocalDateTime expiredAt = now.plusMinutes(Math.max(1, expireMinutes));

        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Locale", "vn");
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", String.valueOf(order.getId()));
        params.put("vnp_OrderInfo", VnpayUtil.sanitizeOrderInfo("Thanh toan don hang #" + order.getId()));
        params.put("vnp_OrderType", "other");
        params.put("vnp_Amount", String.valueOf(toVnpayAmount(order.getTotal())));
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", VnpayUtil.normalizeIpAddress(clientIp));
        params.put("vnp_CreateDate", VNPAY_DATE_FORMAT.format(now));
        params.put("vnp_ExpireDate", VNPAY_DATE_FORMAT.format(expiredAt));
        if (qrOnly) {
            params.put("vnp_BankCode", "VNPAYQR");
        }

        String hashData = VnpayUtil.buildHashData(params);
        String secureHash = VnpayUtil.hmacSHA512(hashSecret, hashData);
        String queryString = VnpayUtil.buildQueryString(params) + "&vnp_SecureHash=" + secureHash;

        VnpayCreatePaymentResponse response = new VnpayCreatePaymentResponse();
        response.setOrderId(order.getId());
        response.setTransactionRef(String.valueOf(order.getId()));
        response.setProvider("VNPAY");
        response.setQrOnly(qrOnly);
        response.setExpiresAt(expiredAt.toString());
        response.setPaymentUrl(payUrl + "?" + queryString);
        return response;
    }

    public VnpayProcessResult processIpnCallback(Map<String, String> rawParams) {
        VnpayProcessResult result = verifyCallback(rawParams);
        if (!result.isValidSignature() || !result.isOrderFound() || !result.isAmountValid()) {
            return result;
        }

        if (result.isSuccessfulPayment()) {
            Order order = findOrder(result.getOrderId());
            if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                result.setAlreadyConfirmed(true);
                result.setMessage("Order already confirmed");
                return result;
            }

            UpdateOrderPaymentStatusRequest request = new UpdateOrderPaymentStatusRequest();
            request.setPaymentStatus("PAID");
            orderService.updatePaymentStatus(order.getId(), request);
            result.setMessage("Confirm Success");
            return result;
        }

        result.setMessage("Payment not successful");
        return result;
    }

    public VnpayProcessResult verifyCallback(Map<String, String> rawParams) {
        VnpayProcessResult result = new VnpayProcessResult();
        result.setMessage("Invalid request");

        if (rawParams == null || rawParams.isEmpty()) {
            return result;
        }

        String secureHash = rawParams.get("vnp_SecureHash");
        if (secureHash == null || secureHash.isBlank()) {
            result.setMessage("Invalid signature");
            return result;
        }

        Map<String, String> fields = new HashMap<>();
        rawParams.forEach((key, value) -> {
            if (key != null && key.startsWith("vnp_") && value != null && !value.isBlank()) {
                fields.put(key, value);
            }
        });
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signValue = VnpayUtil.hmacSHA512(hashSecret, VnpayUtil.buildHashData(fields));
        if (!Objects.equals(signValue, secureHash)) {
            result.setMessage("Invalid signature");
            return result;
        }

        result.setValidSignature(true);
        result.setTransactionRef(rawParams.get("vnp_TxnRef"));
        result.setResponseCode(rawParams.get("vnp_ResponseCode"));
        result.setTransactionStatus(rawParams.get("vnp_TransactionStatus"));

        Integer orderId;
        try {
            orderId = Integer.valueOf(String.valueOf(rawParams.get("vnp_TxnRef")).trim());
        } catch (Exception e) {
            result.setMessage("Order not found");
            return result;
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            result.setOrderId(orderId);
            result.setMessage("Order not found");
            return result;
        }

        result.setOrderFound(true);
        result.setOrderId(order.getId());

        long requestAmount;
        try {
            requestAmount = Long.parseLong(String.valueOf(rawParams.get("vnp_Amount")));
        } catch (Exception e) {
            result.setMessage("invalid amount");
            return result;
        }

        long expectedAmount = toVnpayAmount(order.getTotal());
        if (requestAmount != expectedAmount) {
            result.setMessage("invalid amount");
            return result;
        }

        result.setAmountValid(true);

        boolean success = "00".equals(rawParams.get("vnp_ResponseCode"))
                && "00".equals(rawParams.get("vnp_TransactionStatus"));
        result.setSuccessfulPayment(success);
        result.setMessage(success ? "Confirm Success" : "Payment not successful");
        return result;
    }

    private long toVnpayAmount(BigDecimal total) {
        BigDecimal safeAmount = total == null ? BigDecimal.ZERO : total;
        return safeAmount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }

    private void validateConfig() {
        if (tmnCode == null || tmnCode.isBlank() || hashSecret == null || hashSecret.isBlank()) {
            throw new RuntimeException("Thiếu cấu hình VNPAY. Hãy bổ sung vnpay.tmn-code và vnpay.hash-secret trong application.properties");
        }
    }

    private Order findOrder(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
    }

    private void validateOrderForPayment(Order order) {
        String status = String.valueOf(order.getStatus()).trim().toUpperCase();
        String paymentStatus = String.valueOf(order.getPaymentStatus()).trim().toUpperCase();

        if ("PAID".equals(paymentStatus)) {
            throw new RuntimeException("Đơn hàng này đã được thanh toán rồi");
        }

        if ("HUY".equals(status) || "TRA_HANG".equals(status)) {
            throw new RuntimeException("Đơn hàng hiện tại không thể thanh toán qua VNPAY");
        }

        if (order.getTotal() == null || order.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền đơn hàng không hợp lệ để thanh toán VNPAY");
        }
    }
}
