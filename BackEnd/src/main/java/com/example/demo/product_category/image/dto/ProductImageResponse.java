package com.example.demo.product_category.image.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageResponse {
    private Integer id;
    private String imageUrl;
    private Boolean avatar;
    private Integer sortOrder;
}
