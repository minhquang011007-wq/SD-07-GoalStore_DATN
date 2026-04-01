package com.example.demo.order.service;

import com.example.demo.order.dto.*;

import java.util.List;

public interface OrderService {

    List<OrderResponse> getAllOrders();

    List<OrderResponse> getOrdersByStatus(String status);

    List<OrderResponse> getOrdersByCustomer(Integer customerId);

    OrderDetailResponse getOrderDetail(Integer id);

    OrderDetailResponse createOrder(CreateOrderRequest request);

    OrderDetailResponse updateOrder(Integer id, UpdateOrderRequest request);

    OrderDetailResponse updateStatus(Integer id, UpdateOrderStatusRequest request);

    OrderDetailResponse cancelOrder(Integer id);

    OrderDetailResponse returnOrder(Integer id, ReturnOrderRequest request);

    void deleteOrder(Integer id);
}