package com.example.demo.order_return.mapper;

import com.example.demo.order_return.dto.OrderItemResponse;
import com.example.demo.order_return.dto.OrderResponse;
import com.example.demo.order_return.entity.Order;
import com.example.demo.order_return.entity.OrderItem;
import com.example.demo.product_category.image.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream().map(this::toItemResponse).toList();

        return OrderResponse.builder()
                .id(order.getId())
                .code(order.getCode())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomer() != null ? order.getCustomer().getTen() : null)
                .staffId(order.getStaff() != null ? order.getStaff().getId() : null)
                .staffUsername(order.getStaff() != null ? order.getStaff().getUsername() : null)
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null)
                .paymentStatus(order.getPaymentStatus())
                .channel(order.getChannel() != null ? order.getChannel().name() : null)
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .subtotal(order.getSubtotal())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .total(order.getTotal())
                .orderDate(order.getOrderDate())
                .items(itemResponses)
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        BigDecimal lineTotal = item.getLineTotal() != null
                ? item.getLineTotal()
                : item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return OrderItemResponse.builder()
                .itemId(item.getId())
                .productId(item.getVariant() != null && item.getVariant().getProduct() != null ? item.getVariant().getProduct().getId() : null)
                .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                .sku(item.getVariant() != null ? item.getVariant().getSku() : null)
                .productName(item.getVariant() != null && item.getVariant().getProduct() != null ? item.getVariant().getProduct().getName() : null)
                .imageUrl(resolveProductImage(item))
                .size(item.getVariant() != null ? item.getVariant().getSize() : null)
                .color(item.getVariant() != null ? item.getVariant().getColor() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(lineTotal)
                .build();
    }

    private String resolveProductImage(OrderItem item) {
        if (item.getVariant() == null || item.getVariant().getProduct() == null || item.getVariant().getProduct().getImages() == null) {
            return null;
        }

        return item.getVariant().getProduct().getImages().stream()
                .sorted((a, b) -> {
                    int avatarCompare = Boolean.compare(Boolean.TRUE.equals(b.getAvatar()), Boolean.TRUE.equals(a.getAvatar()));
                    if (avatarCompare != 0) return avatarCompare;
                    int sortA = a.getSortOrder() == null ? 0 : a.getSortOrder();
                    int sortB = b.getSortOrder() == null ? 0 : b.getSortOrder();
                    return Integer.compare(sortA, sortB);
                })
                .map(ProductImage::getImageUrl)
                .filter(url -> url != null && !url.isBlank())
                .findFirst()
                .orElse(null);
    }
}
