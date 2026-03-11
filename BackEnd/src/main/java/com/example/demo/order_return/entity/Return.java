package com.example.demo.order_return.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Returns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "reason")
    private String reason;

    @Column(name = "refund_total")
    private BigDecimal refundTotal;

    @Column(name = "note")
    private String note;
}