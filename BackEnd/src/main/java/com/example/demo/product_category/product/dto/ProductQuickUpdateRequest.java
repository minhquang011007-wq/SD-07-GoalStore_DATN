package com.example.demo.product_category.product.dto;

import com.example.demo.product_category.common.enums.ProductDisplayStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductQuickUpdateRequest {
    private String name;
    private ProductDisplayStatus displayStatus;
    private String brand;
    private String material;
    private String season;
}
