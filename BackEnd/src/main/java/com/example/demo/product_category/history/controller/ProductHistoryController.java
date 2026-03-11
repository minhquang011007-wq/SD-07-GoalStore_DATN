package com.example.demo.product_category.history.controller;

import com.example.demo.product_category.history.dto.ProductHistoryResponse;
import com.example.demo.product_category.history.service.ProductHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductHistoryController {
    private final ProductHistoryService productHistoryService;

    @GetMapping("/{productId}/history")
    public List<ProductHistoryResponse> findByProduct(@PathVariable Integer productId) {
        return productHistoryService.findByProduct(productId);
    }
}
