package com.example.demo.order_return.entity;

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

    @Column(name = "sku")
    private String sku;

    @Column(name = "gia_ban")
    private BigDecimal giaBan;

    @Column(name = "gia_khuyen_mai")
    private BigDecimal giaKhuyenMai;

    @Column(name = "ton_kho")
    private Integer tonKho;

    @Column(name = "size")
    private String size;

    @Column(name = "trang_thai")
    private String trangThai;
}