package com.example.demo.product_category.product_public.controller;

import com.example.demo.product_category.common.dto.PageResponse;
import com.example.demo.product_category.common.enums.ProductType;
import com.example.demo.product_category.common.enums.TargetGender;
import com.example.demo.product_category.product_public.dto.PublicProductDetailResponse;
import com.example.demo.product_category.product_public.dto.PublicProductSummaryResponse;
import com.example.demo.product_category.product_public.service.PublicProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/products")
@RequiredArgsConstructor
public class PublicProductController {
    private final PublicProductService publicProductService;

    @GetMapping
    public PageResponse<PublicProductSummaryResponse> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryIds,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) ProductType productType,
            @RequestParam(required = false) TargetGender targetGender,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "newest") String sort) {
        return publicProductService.search(page, size, keyword, parseIds(categoryIds), brand, productType,
                targetGender, minPrice, maxPrice, inStock, sort);
    }

    @GetMapping("/{id}")
    public PublicProductDetailResponse getDetail(@PathVariable Integer id) {
        return publicProductService.getDetail(id);
    }

    private Set<Integer> parseIds(String raw) {
        if (raw == null || raw.isBlank()) return Collections.emptySet();
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Integer::valueOf)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
