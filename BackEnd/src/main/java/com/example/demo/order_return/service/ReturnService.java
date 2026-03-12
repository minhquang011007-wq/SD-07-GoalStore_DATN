package com.example.demo.order_return.service;

import com.example.demo.order_return.dto.CreateReturnRequest;
import com.example.demo.order_return.dto.ReturnResponse;

public interface ReturnService {
    ReturnResponse createReturn(CreateReturnRequest request);
    ReturnResponse getByOrderId(Integer orderId);
}