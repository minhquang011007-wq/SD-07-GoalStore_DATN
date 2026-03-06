package com.example.demo.product_category.variant.dto;

import com.example.demo.product_category.common.enums.VariantStockStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantResponse {
    private Integer id;
    private String sku;
    private String size;
    private String color;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Integer stockQuantity;
    private VariantStockStatus stockStatus;
}
