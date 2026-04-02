package com.example.demo.order.controller;

import com.example.demo.order.dto.*;
import com.example.demo.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer customerId
    ) {
        if (customerId != null) {
            return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
        }
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(orderService.getOrdersByStatus(status));
        }
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrderDetail(id));
    }

    @PostMapping
    public ResponseEntity<OrderDetailResponse> createOrder(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> updateOrder(
            @PathVariable Integer id,
            @RequestBody UpdateOrderRequest request
    ) {
        return ResponseEntity.ok(orderService.updateOrder(id, request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDetailResponse> updateStatus(
            @PathVariable Integer id,
            @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(orderService.updateStatus(id, request));
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<OrderDetailResponse> updatePaymentStatus(
            @PathVariable Integer id,
            @RequestBody UpdateOrderPaymentStatusRequest request
    ) {
        return ResponseEntity.ok(orderService.updatePaymentStatus(id, request));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderDetailResponse> cancelOrder(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<OrderDetailResponse> returnOrder(
            @PathVariable Integer id,
            @RequestBody(required = false) ReturnOrderRequest request
    ) {
        return ResponseEntity.ok(orderService.returnOrder(id, request));
    }

    // FE bán online hiện đang gọi DELETE để hủy đơn, không phải xóa cứng.
    // Vì vậy map DELETE sang cancelOrder để đặt hàng/hủy đơn không bị vỡ luồng.
    @DeleteMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> deleteOrder(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}
