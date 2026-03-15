package com.example.demo.product_category.product_public.dto;

import com.example.demo.product_category.common.enums.ProductType;
import com.example.demo.product_category.common.enums.TargetGender;
import com.example.demo.product_category.common.enums.VariantStockStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
public class PublicProductSummaryResponse {
    private Integer id;
    private String name;
    private String baseSku;
    private String brand;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal salePrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer totalStock;
    private VariantStockStatus stockStatus;
    private ProductType productType;
    private TargetGender targetGender;
    @Builder.Default
    private Set<String> categoryNames = new LinkedHashSet<>();
}
