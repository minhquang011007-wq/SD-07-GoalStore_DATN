package com.example.demo.order_return.service;

import com.example.demo.order_return.dto.CreateReturnRequest;
import com.example.demo.order_return.dto.ReturnResponse;
import com.example.demo.order_return.entity.Order;
import com.example.demo.order_return.entity.OrderItem;
import com.example.demo.order_return.entity.OrderStatus;
import com.example.demo.order_return.entity.ProductVariant;
import com.example.demo.order_return.entity.Return;
import com.example.demo.order_return.mapper.ReturnMapper;
import com.example.demo.order_return.repository.OrderRepository;
import com.example.demo.order_return.repository.ProductVariantRepository;
import com.example.demo.order_return.repository.ReturnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ReturnMapper returnMapper;
    private final AuditLogService auditLogService;

    @Transactional
    public ReturnResponse createReturn(Integer orderId, CreateReturnRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (returnRepository.findByOrderId(orderId).isPresent()) {
            throw new RuntimeException("Đơn hàng này đã có phiếu trả hàng");
        }

        if (order.getStatus() == OrderStatus.HUY) {
            throw new RuntimeException("Đơn đã hủy, không thể trả hàng");
        }

        BigDecimal refundTotal = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getVariant();
            variant.setTonKho(variant.getTonKho() + item.getQuantity());
            productVariantRepository.save(variant);

            refundTotal = refundTotal.add(
                    item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }

        order.setStatus(OrderStatus.TRA_HANG);
        orderRepository.save(order);

        Return ret = Return.builder()
                .order(order)
                .reason(request.getReason())
                .note(request.getNote())
                .refundTotal(refundTotal)
                .build();

        Return saved = returnRepository.save(ret);

        auditLogService.log(
                order.getStaff(),
                "CREATE_RETURN",
                "RETURN",
                saved.getId(),
                "Tạo phiếu trả hàng cho order " + orderId + ", refund = " + refundTotal
        );

        return returnMapper.toResponse(saved);
    }

    public List<ReturnResponse> getAllReturns() {
        return returnRepository.findAll()
                .stream()
                .map(returnMapper::toResponse)
                .toList();
    }

    public ReturnResponse getReturnById(Integer id) {
        Return ret = returnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu trả hàng"));
        return returnMapper.toResponse(ret);
    }
}