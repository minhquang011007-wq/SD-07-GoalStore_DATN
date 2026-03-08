package com.example.demo.product_category.variant.service;

import com.example.demo.product_category.common.enums.VariantStockStatus;
import com.example.demo.product_category.variant.dto.ProductVariantRequest;
import com.example.demo.product_category.variant.dto.ProductVariantResponse;

import java.util.List;

public interface ProductVariantService {
    ProductVariantResponse create(Integer productId, ProductVariantRequest request);
    ProductVariantResponse update(Integer id, ProductVariantRequest request);
    void delete(Integer id);
    ProductVariantResponse getDetail(Integer id);
    List<ProductVariantResponse> findByProduct(Integer productId);
    List<ProductVariantResponse> search(Integer productId, String keyword, VariantStockStatus stockStatus);
}
