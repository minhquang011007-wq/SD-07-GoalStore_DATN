package com.example.demo.order_return.controller;

import com.example.demo.order_return.dto.CreateReturnRequest;
import com.example.demo.order_return.dto.ReturnResponse;
import com.example.demo.order_return.service.ReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping("/order/{orderId}")
    public ReturnResponse createReturn(@PathVariable Integer orderId,
                                       @Valid @RequestBody CreateReturnRequest request) {
        return returnService.createReturn(orderId, request);
    }

    @GetMapping
    public List<ReturnResponse> getAllReturns() {
        return returnService.getAllReturns();
    }

    @GetMapping("/{id}")
    public ReturnResponse getReturnById(@PathVariable Integer id) {
        return returnService.getReturnById(id);
    }
}