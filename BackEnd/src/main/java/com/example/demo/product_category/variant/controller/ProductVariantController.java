package com.example.demo.product_category.variant.controller;

import com.example.demo.product_category.common.enums.VariantStockStatus;
import com.example.demo.product_category.variant.dto.ProductVariantRequest;
import com.example.demo.product_category.variant.dto.ProductVariantResponse;
import com.example.demo.product_category.variant.service.ProductVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService variantService;

    @PostMapping("/api/products/{productId}/variants")
    public ResponseEntity<ProductVariantResponse> create(
            @PathVariable Integer productId,
            @Valid @RequestBody ProductVariantRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(variantService.create(productId, request));
    }

    @PutMapping("/api/variants/{id}")
    public ProductVariantResponse update(@PathVariable Integer id, @Valid @RequestBody ProductVariantRequest request) {
        return variantService.update(id, request);
    }

    @DeleteMapping("/api/variants/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        variantService.delete(id);
    }

    @GetMapping("/api/variants/{id}")
    public ProductVariantResponse getDetail(@PathVariable Integer id) {
        return variantService.getDetail(id);
    }

    @GetMapping("/api/products/{productId}/variants")
    public List<ProductVariantResponse> findByProduct(
            @PathVariable Integer productId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) VariantStockStatus stockStatus
    ) {
        if ((keyword != null && !keyword.isBlank()) || stockStatus != null) {
            return variantService.search(productId, keyword, stockStatus);
        }
        return variantService.findByProduct(productId);
    }
}
