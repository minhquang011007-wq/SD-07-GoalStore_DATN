package com.example.demo.product_category.tag.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponse {
    private Integer id;
    private String name;
    private String description;
}
