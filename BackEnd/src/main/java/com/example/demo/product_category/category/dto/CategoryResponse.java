package com.example.demo.product_category.category.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Integer id;
    private String name;
    private String description;
    private String imageUrl;
    private long productCount;
}
