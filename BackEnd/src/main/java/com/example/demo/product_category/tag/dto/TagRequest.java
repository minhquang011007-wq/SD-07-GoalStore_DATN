package com.example.demo.product_category.tag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagRequest {
    @NotBlank(message = "Tên tag không được để trống")
    private String name;
    private String description;
}
