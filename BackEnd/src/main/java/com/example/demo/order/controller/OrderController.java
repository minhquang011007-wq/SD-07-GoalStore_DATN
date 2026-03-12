package com.example.demo.order.controller;

import com.example.demo.order.dto.CreateOrderRequest;
import com.example.demo.order.dto.OrderResponse;
import com.example.demo.order.dto.UpdateOrderItemRequest;
import com.example.demo.order.dto.UpdateOrderStatusRequest;
import com.example.demo.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Integer id,
                                                      @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request.getStatus()));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponse> addItem(@PathVariable Integer id,
                                                 @RequestParam Integer variantId,
                                                 @RequestParam Integer quantity) {
        return ResponseEntity.ok(orderService.addItem(id, variantId, quantity));
    }

    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<OrderResponse> updateItem(@PathVariable Integer id,
                                                    @PathVariable Integer itemId,
                                                    @RequestBody UpdateOrderItemRequest request) {
        return ResponseEntity.ok(orderService.updateItem(id, itemId, request));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<String> deleteItem(@PathVariable Integer id,
                                             @PathVariable Integer itemId) {
        orderService.deleteItem(id, itemId);
        return ResponseEntity.ok("Xóa sản phẩm khỏi đơn hàng thành công");
    }
}