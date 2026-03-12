package com.example.demo.product_category.attribute.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductAttributeRequest {
    @NotBlank(message = "Tên thuộc tính không được để trống")
    private String name;

    @NotBlank(message = "Giá trị thuộc tính không được để trống")
    private String value;

    private Integer sortOrder;
}
