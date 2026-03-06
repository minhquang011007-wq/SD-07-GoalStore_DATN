package com.example.demo.product_category.product.service;

import com.example.demo.product_category.common.dto.PageResponse;
import com.example.demo.product_category.common.enums.ProductDisplayStatus;
import com.example.demo.product_category.common.enums.VariantStockStatus;
import com.example.demo.product_category.product.dto.*;

import java.util.List;
import java.util.Set;

public interface ProductService {
    ProductDetailResponse create(ProductRequest request);
    ProductDetailResponse update(Integer id, ProductRequest request);
    ProductDetailResponse quickUpdate(Integer id, ProductQuickUpdateRequest request);
    List<ProductDetailResponse> batchUpdate(ProductBatchUpdateRequest request);
    void delete(Integer id);
    ProductDetailResponse getDetail(Integer id);
    PageResponse<ProductSummaryResponse> search(int page, int size, String keyword, Set<Integer> categoryIds,
                                               Set<Integer> tagIds, ProductDisplayStatus displayStatus, String brand,
                                               String material, Boolean inStock, VariantStockStatus stockStatus,
                                               Boolean hideOutOfStock, Integer createdWithinDays, String sort);
    List<ProductSummaryResponse> topSelling();
    List<ProductSummaryResponse> newest(int limit);
}
