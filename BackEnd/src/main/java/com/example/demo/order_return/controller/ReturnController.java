package com.example.demo.order_return.controller;

import com.example.demo.order_return.dto.CreateReturnRequest;
import com.example.demo.order_return.dto.ReturnResponse;
import com.example.demo.order_return.service.ReturnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
@CrossOrigin("*")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @PostMapping
    public ResponseEntity<ReturnResponse> createReturn(@RequestBody CreateReturnRequest request) {
        return ResponseEntity.ok(returnService.createReturn(request));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ReturnResponse> getByOrderId(@PathVariable Integer orderId) {
        return ResponseEntity.ok(returnService.getByOrderId(orderId));
    }
}