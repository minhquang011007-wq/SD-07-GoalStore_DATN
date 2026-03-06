package com.example.demo.product_category.image.entity;

import com.example.demo.product_category.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Product_Images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "is_avatar", nullable = false)
    private Boolean avatar;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @PrePersist
    public void prePersist() {
        if (avatar == null) {
            avatar = false;
        }
        if (sortOrder == null) {
            sortOrder = 0;
        }
    }
}
