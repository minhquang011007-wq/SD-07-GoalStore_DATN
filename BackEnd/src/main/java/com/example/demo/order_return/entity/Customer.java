package com.example.demo.order_return.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten")
    private String ten;

    @Column(name = "email")
    private String email;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "diem_tich_luy")
    private Integer diemTichLuy;

    @Column(name = "loai_khach")
    private String loaiKhach;
}