package com.example.demo.product_category.tag.entity;

import com.example.demo.product_category.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "Tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_tag", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "mo_ta", length = 255)
    private String description;

    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    private Set<Product> products = new LinkedHashSet<>();
}
