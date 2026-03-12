package com.example.demo.order.service;

import com.example.demo.order.dto.CreateOrderRequest;
import com.example.demo.order.dto.OrderResponse;
import com.example.demo.order.dto.UpdateOrderItemRequest;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Integer id);
    OrderResponse updateStatus(Integer orderId, String status);
    OrderResponse addItem(Integer orderId, Integer variantId, Integer quantity);
    OrderResponse updateItem(Integer orderId, Integer orderItemId, UpdateOrderItemRequest request);
    void deleteItem(Integer orderId, Integer orderItemId);
}