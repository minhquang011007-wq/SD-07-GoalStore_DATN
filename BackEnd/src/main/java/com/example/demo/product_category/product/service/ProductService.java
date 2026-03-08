package com.example.demo.product_category.product.service;

import com.example.demo.product_category.common.dto.PageResponse;
import com.example.demo.product_category.common.enums.ProductDisplayStatus;
import com.example.demo.product_category.common.enums.ProductType;
import com.example.demo.product_category.common.enums.TargetGender;
import com.example.demo.product_category.common.enums.VariantStockStatus;
import com.example.demo.product_category.product.dto.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface ProductService {
    ProductDetailResponse create(ProductRequest request);
    ProductDetailResponse update(Integer id, ProductRequest request);
    ProductDetailResponse quickUpdate(Integer id, ProductQuickUpdateRequest request);
    List<ProductDetailResponse> batchUpdate(ProductBatchUpdateRequest request);
    void delete(Integer id);
    void hardDelete(Integer id);
    ProductDetailResponse hideWhenOutOfStock(Integer id);
    ProductDetailResponse getDetail(Integer id);
    PageResponse<ProductSummaryResponse> search(int page, int size, String keyword, Set<Integer> categoryIds,
                                                Set<Integer> tagIds, ProductDisplayStatus displayStatus, String brand,
                                                String material, Boolean inStock, VariantStockStatus stockStatus,
                                                Boolean hideOutOfStock, Integer createdWithinDays, String sort,
                                                ProductType productType, TargetGender targetGender,
                                                Integer releaseYear, BigDecimal minPrice, BigDecimal maxPrice,
                                                Boolean categoryMatchAll);
    List<ProductSummaryResponse> topSelling();
    List<ProductSummaryResponse> newest(int limit);
    List<ProductSummaryResponse> findByCategory(Integer categoryId);
    List<ProductSummaryResponse> findByTag(Integer tagId);
}
