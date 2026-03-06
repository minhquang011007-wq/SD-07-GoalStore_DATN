package com.example.demo.product_category.category.entity;

import com.example.demo.product_category.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "Categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_danh_muc", nullable = false, unique = true, length = 150)
    private String name;

    @Column(name = "mo_ta", length = 500)
    private String description;

    @Column(name = "hinh_anh", length = 255)
    private String imageUrl;

    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    private Set<Product> products = new LinkedHashSet<>();
}
