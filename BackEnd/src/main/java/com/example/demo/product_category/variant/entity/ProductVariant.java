package com.example.demo.product_category.variant.entity;

import com.example.demo.product_category.common.enums.VariantStockStatus;
import com.example.demo.product_category.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Product_Variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "sku", nullable = false, unique = true, length = 80)
    private String sku;

    @Column(name = "size", nullable = false, length = 10)
    private String size;

    @Column(name = "mau_sac", nullable = false, length = 50)
    private String color;

    @Column(name = "gia_ban", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "gia_khuyen_mai", precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "ton_kho", nullable = false)
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    private VariantStockStatus stockStatus;

    @PrePersist
    public void prePersist() {
        if (stockQuantity == null) {
            stockQuantity = 0;
        }
        if (stockStatus == null) {
            stockStatus = stockQuantity > 0 ? VariantStockStatus.CON_HANG : VariantStockStatus.HET_HANG;
        }
    }
}
