package com.example.demo.product_category.attribute.controller;

import com.example.demo.product_category.attribute.dto.ProductAttributeRequest;
import com.example.demo.product_category.attribute.dto.ProductAttributeResponse;
import com.example.demo.product_category.attribute.service.ProductAttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductAttributeController {

    private final ProductAttributeService attributeService;

    @PostMapping("/api/products/{productId}/attributes")
    public ResponseEntity<ProductAttributeResponse> create(
            @PathVariable Integer productId,
            @Valid @RequestBody ProductAttributeRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attributeService.create(productId, request));
    }

    @PutMapping("/api/attributes/{id}")
    public ProductAttributeResponse update(@PathVariable Integer id, @Valid @RequestBody ProductAttributeRequest request) {
        return attributeService.update(id, request);
    }

    @DeleteMapping("/api/attributes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        attributeService.delete(id);
    }

    @GetMapping("/api/attributes/{id}")
    public ProductAttributeResponse getDetail(@PathVariable Integer id) {
        return attributeService.getDetail(id);
    }

    @GetMapping("/api/products/{productId}/attributes")
    public List<ProductAttributeResponse> findByProduct(@PathVariable Integer productId) {
        return attributeService.findByProduct(productId);
    }
}
