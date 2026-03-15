package com.example.demo.order_return.controller;

import com.example.demo.order_return.dto.CreateCheckoutRequest;
import com.example.demo.order_return.dto.CreateOrderRequest;
import com.example.demo.order_return.dto.OrderResponse;
import com.example.demo.order_return.dto.UpdateOrderItemRequest;
import com.example.demo.order_return.dto.UpdateOrderStatusRequest;
import com.example.demo.order_return.entity.OrderStatus;
import com.example.demo.order_return.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @PostMapping("/checkout")
    public OrderResponse checkout(@Valid @RequestBody CreateCheckoutRequest request) {
        return orderService.createOrderFromCart(request);
    }

    @GetMapping
    public List<OrderResponse> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String date
    ) {
        if (status != null) {
            return orderService.getOrdersByStatus(OrderStatus.valueOf(status.toUpperCase()));
        }
        if (customerId != null) {
            return orderService.getOrdersByCustomer(customerId);
        }
        if (channel != null) {
            return orderService.getOrdersByChannel(channel);
        }
        if (date != null) {
            return orderService.getOrdersByDate(LocalDate.parse(date));
        }
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Integer id) {
        return orderService.getOrderById(id);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Integer id,
                                      @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateOrderStatus(id, request.getStatus());
    }

    @PatchMapping("/{orderId}/items/{itemId}")
    public OrderResponse updateOrderItem(@PathVariable Integer orderId,
                                         @PathVariable Integer itemId,
                                         @Valid @RequestBody UpdateOrderItemRequest request) {
        return orderService.updateOrderItem(orderId, itemId, request);
    }

    @DeleteMapping("/{id}")
    public String cancelOrder(@PathVariable Integer id) {
        orderService.cancelOrder(id);
        return "Hủy đơn hàng thành công";
    }
}
