package com.example.demo.product_category.product.dto;

import com.example.demo.product_category.common.enums.ProductDisplayStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBatchUpdateItemRequest {
    @NotNull
    private Integer id;
    private ProductDisplayStatus displayStatus;
    private String brand;
    private String season;
    private String material;
}
