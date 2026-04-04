package com.example.demo.voucher.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerVoucherResponse {

    private Integer id;
    private String code;
    private String name;
    private String description;
    private BigDecimal discountPercent;
    private BigDecimal minOrderAmount;
    private Integer totalQuantity;
    private Integer remainingQuantity;
    private Boolean isActive;
    private Boolean claimed;
    private Boolean used;
    private LocalDateTime claimedAt;
    private LocalDateTime usedAt;
    private Integer usedOrderId;
    private String usedOrderCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getClaimed() {
        return claimed;
    }

    public void setClaimed(Boolean claimed) {
        this.claimed = claimed;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public LocalDateTime getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(LocalDateTime claimedAt) {
        this.claimedAt = claimedAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public Integer getUsedOrderId() {
        return usedOrderId;
    }

    public void setUsedOrderId(Integer usedOrderId) {
        this.usedOrderId = usedOrderId;
    }

    public String getUsedOrderCode() {
        return usedOrderCode;
    }

    public void setUsedOrderCode(String usedOrderCode) {
        this.usedOrderCode = usedOrderCode;
    }
}
