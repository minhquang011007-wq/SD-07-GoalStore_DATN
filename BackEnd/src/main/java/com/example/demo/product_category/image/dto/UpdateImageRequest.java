package com.example.demo.product_category.image.dto;

import lombok.Data;

@Data
public class UpdateImageRequest {
    private Boolean avatar;
    private Integer sortOrder;
}
