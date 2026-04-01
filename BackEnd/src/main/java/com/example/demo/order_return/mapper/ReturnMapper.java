package com.example.demo.order_return.mapper;

import com.example.demo.order_return.dto.ReturnResponse;
import com.example.demo.order_return.entity.Return;
import org.springframework.stereotype.Component;

@Component
public class ReturnMapper {

    public ReturnResponse toResponse(Return ret) {
        return ReturnResponse.builder()
                .id(ret.getId())
                .orderId(ret.getOrder() != null ? ret.getOrder().getId() : null)
                .reason(ret.getReason())
                .note(ret.getNote())
                .refundTotal(ret.getRefundTotal())
                .returnDate(ret.getReturnDate())
                .build();
    }
}