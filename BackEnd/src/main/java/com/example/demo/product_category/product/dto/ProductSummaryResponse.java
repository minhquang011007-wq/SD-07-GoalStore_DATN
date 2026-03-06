package com.example.demo.product_category.product.dto;

import com.example.demo.product_category.common.enums.ProductDisplayStatus;
import com.example.demo.product_category.common.enums.ProductType;
import com.example.demo.product_category.common.enums.TargetGender;
import com.example.demo.product_category.common.enums.VariantStockStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSummaryResponse {
    private Integer id;
    private String name;
    private String baseSku;
    private String brand;
    private String season;
    private ProductType productType;
    private TargetGender targetGender;
    private String material;
    private ProductDisplayStatus displayStatus;
    private String thumbnailUrl;
    private Set<String> categoryNames;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer totalStock;
    private VariantStockStatus stockStatus;
    private Long soldQuantity;
    private LocalDateTime createdAt;
}
