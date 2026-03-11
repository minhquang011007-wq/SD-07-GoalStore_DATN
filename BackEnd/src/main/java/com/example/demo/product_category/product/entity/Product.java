package com.example.demo.product_category.product.entity;

import com.example.demo.product_category.category.entity.Category;
import com.example.demo.product_category.common.enums.ProductDisplayStatus;
import com.example.demo.product_category.common.enums.ProductType;
import com.example.demo.product_category.common.enums.TargetGender;
import com.example.demo.product_category.image.entity.ProductImage;
import com.example.demo.product_category.tag.entity.Tag;
import com.example.demo.product_category.variant.entity.ProductVariant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "Products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_san_pham", nullable = false, length = 200)
    private String name;

    @Column(name = "sku_chuan", nullable = false, unique = true, length = 100)
    private String baseSku;

    @Column(name = "thuong_hieu", length = 100)
    private String brand;

    @Column(name = "mua_bo_suu_tap", length = 100)
    private String season;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_san_pham", length = 30)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    @Column(name = "doi_tuong", length = 30)
    private TargetGender targetGender;

    @Column(name = "chat_lieu", length = 100)
    private String material;

    @Lob
    @Column(name = "mo_ta")
    private String description;

    @Column(name = "nam_phien_ban")
    private Integer releaseYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    private ProductDisplayStatus displayStatus;

    @Column(name = "is_deleted", nullable = false)
    private Boolean deleted;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "Product_Categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
            name = "Product_Tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductVariant> variants = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder asc, id asc")
    @Builder.Default
    private Set<ProductImage> images = new LinkedHashSet<>();

    @PrePersist
    public void prePersist() {
        if (displayStatus == null) {
            displayStatus = ProductDisplayStatus.HIENTHI;
        }
        if (deleted == null) {
            deleted = false;
        }
    }
}
