package com.example.demo.order_return.service.impl;

import com.example.demo.common.exception.BadRequestException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.customer.entity.Customer;
import com.example.demo.order_return.entity.Order;
import com.example.demo.order_return.entity.OrderItem;
import com.example.demo.order_return.repository.OrderItemRepository;
import com.example.demo.order_return.dto.CreateReturnRequest;
import com.example.demo.order_return.dto.ReturnResponse;
import com.example.demo.order_return.entity.Return;
import com.example.demo.order_return.repository.OrderRepository;
import com.example.demo.order_return.repository.ReturnRepository;
import com.example.demo.order_return.service.ReturnService;
import com.example.demo.product_category.variant.entity.ProductVariant;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRepository returnRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public ReturnServiceImpl(ReturnRepository returnRepository,
                             OrderRepository orderRepository,
                             OrderItemRepository orderItemRepository) {
        this.returnRepository = returnRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public ReturnResponse createReturn(CreateReturnRequest request) {
        if (request.getOrderId() == null) {
            throw new BadRequestException("orderId không được để trống");
        }

        if (returnRepository.existsByOrderId(request.getOrderId())) {
            throw new BadRequestException("Đơn hàng này đã được trả hàng");
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        if ("HUY".equalsIgnoreCase(order.getStatus())) {
            throw new BadRequestException("Đơn hàng đã hủy, không thể trả hàng");
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        if (items.isEmpty()) {
            throw new BadRequestException("Đơn hàng không có sản phẩm để trả");
        }

        BigDecimal refundTotal = BigDecimal.ZERO;

        for (OrderItem item : items) {
            ProductVariant variant = item.getVariant();
            variant.setTonKho(variant.getTonKho() + item.getQuantity());

            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            refundTotal = refundTotal.add(lineTotal);
        }

        Return ret = new Return();
        ret.setOrder(order);
        ret.setReturnDate(LocalDateTime.now());
        ret.setReason(request.getReason());
        ret.setNote(request.getNote());
        ret.setRefundTotal(refundTotal);
        returnRepository.save(ret);

        order.setStatus("TRA_HANG");
        order.setTotal(BigDecimal.ZERO);

        Customer customer = order.getCustomer();
        if (customer != null && customer.getDiemTichLuy() != null) {
            int minusPoint = refundTotal.divide(BigDecimal.valueOf(1000)).intValue();
            int newPoint = customer.getDiemTichLuy() - minusPoint;
            customer.setDiemTichLuy(Math.max(newPoint, 0));
        }

        orderRepository.save(order);

        ReturnResponse response = new ReturnResponse();
        response.setId(ret.getId());
        response.setOrderId(order.getId());
        response.setReturnDate(ret.getReturnDate());
        response.setReason(ret.getReason());
        response.setNote(ret.getNote());
        response.setRefundTotal(ret.getRefundTotal());

        return response;
    }

    @Override
    public ReturnResponse getByOrderId(Integer orderId) {
        Return ret = returnRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch sử trả hàng của order id=" + orderId));

        ReturnResponse response = new ReturnResponse();
        response.setId(ret.getId());
        response.setOrderId(ret.getOrder().getId());
        response.setReturnDate(ret.getReturnDate());
        response.setReason(ret.getReason());
        response.setNote(ret.getNote());
        response.setRefundTotal(ret.getRefundTotal());

        return response;
    }
}