package com.example.demo.product_category.product_public.service;

import com.example.demo.product_category.common.dto.PageResponse;
import com.example.demo.product_category.common.enums.ProductType;
import com.example.demo.product_category.common.enums.TargetGender;
import com.example.demo.product_category.product_public.dto.PublicProductDetailResponse;
import com.example.demo.product_category.product_public.dto.PublicProductSummaryResponse;

import java.math.BigDecimal;
import java.util.Set;

public interface PublicProductService {
    PageResponse<PublicProductSummaryResponse> search(int page,
                                                      int size,
                                                      String keyword,
                                                      Set<Integer> categoryIds,
                                                      String brand,
                                                      ProductType productType,
                                                      TargetGender targetGender,
                                                      BigDecimal minPrice,
                                                      BigDecimal maxPrice,
                                                      Boolean inStock,
                                                      String sort);

    PublicProductDetailResponse getDetail(Integer id);
}
