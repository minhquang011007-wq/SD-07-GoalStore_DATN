package com.example.demo.product_category.attribute.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttributeResponse {
    private Integer id;
    private Integer productId;
    private String name;
    private String value;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
