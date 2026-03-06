package com.example.demo.product_category.product.controller;

import com.example.demo.product_category.common.dto.PageResponse;
import com.example.demo.product_category.common.enums.ProductDisplayStatus;
import com.example.demo.product_category.common.enums.VariantStockStatus;
import com.example.demo.product_category.product.dto.*;
import com.example.demo.product_category.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDetailResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/{id}")
    public ProductDetailResponse update(@PathVariable Integer id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @PatchMapping("/{id}/quick-update")
    public ProductDetailResponse quickUpdate(@PathVariable Integer id, @RequestBody ProductQuickUpdateRequest request) {
        return productService.quickUpdate(id, request);
    }

    @PatchMapping("/batch-update")
    public List<ProductDetailResponse> batchUpdate(@Valid @RequestBody ProductBatchUpdateRequest request) {
        return productService.batchUpdate(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        productService.delete(id);
    }

    @GetMapping("/{id}")
    public ProductDetailResponse getDetail(@PathVariable Integer id) {
        return productService.getDetail(id);
    }

    @GetMapping
    public PageResponse<ProductSummaryResponse> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryIds,
            @RequestParam(required = false) String tagIds,
            @RequestParam(required = false) ProductDisplayStatus displayStatus,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) VariantStockStatus stockStatus,
            @RequestParam(required = false) Boolean hideOutOfStock,
            @RequestParam(required = false) Integer createdWithinDays,
            @RequestParam(required = false, defaultValue = "newest") String sort) {
        return productService.search(page, size, keyword, parseIds(categoryIds), parseIds(tagIds), displayStatus,
                brand, material, inStock, stockStatus, hideOutOfStock, createdWithinDays, sort);
    }

    @GetMapping("/top-selling")
    public List<ProductSummaryResponse> topSelling() {
        return productService.topSelling();
    }

    @GetMapping("/newest")
    public List<ProductSummaryResponse> newest(@RequestParam(defaultValue = "10") int limit) {
        return productService.newest(limit);
    }

    private Set<Integer> parseIds(String raw) {
        if (raw == null || raw.isBlank()) return Collections.emptySet();
        return Arrays.stream(raw.split(",")).map(String::trim).filter(s -> !s.isBlank())
                .map(Integer::valueOf).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
