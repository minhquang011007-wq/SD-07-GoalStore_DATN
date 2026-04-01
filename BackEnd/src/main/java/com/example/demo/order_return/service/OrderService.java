package com.example.demo.order_return.service;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.cart.entity.Cart;
import com.example.demo.cart.entity.CartItem;
import com.example.demo.cart.repository.CartRepository;
import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.customer_address.entity.CustomerAddress;
import com.example.demo.customer_address.repository.CustomerAddressRepository;
import com.example.demo.order_return.dto.CreateCheckoutRequest;
import com.example.demo.order_return.dto.CreateOrderItemRequest;
import com.example.demo.order_return.dto.CreateOrderRequest;
import com.example.demo.order_return.dto.OrderResponse;
import com.example.demo.order_return.dto.UpdateOrderItemRequest;
import com.example.demo.order_return.entity.*;
import com.example.demo.order_return.mapper.OrderMapper;
import com.example.demo.order_return.repository.OrderItemRepository;
import com.example.demo.order_return.repository.OrderRepository;
import com.example.demo.product_category.common.enums.VariantStockStatus;
import com.example.demo.product_category.variant.entity.ProductVariant;
import com.example.demo.product_category.variant.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final CartRepository cartRepository;
    private final OrderMapper orderMapper;
    private final AuditLogService auditLogService;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        User staff = null;
        if (request.getStaffId() != null) {
            staff = userRepository.findById(request.getStaffId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        }

        Order order = Order.builder()
                .customer(customer)
                .staff(staff)
                .code(generateOrderCode())
                .receiverName(request.getReceiverName() != null ? request.getReceiverName() : customer.getTen())
                .receiverPhone(request.getReceiverPhone() != null ? request.getReceiverPhone() : customer.getSdt())
                .shippingAddress(request.getShippingAddress())
                .note(trimToNull(request.getNote()))
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.MOI)
                .paymentMethod(parsePaymentMethod(request.getPaymentMethod()))
                .paymentStatus(request.getPaymentStatus() == null ? "UNPAID" : request.getPaymentStatus().toUpperCase())
                .channel(parseChannel(request.getChannel()))
                .subtotal(BigDecimal.ZERO)
                .shippingFee(defaultMoney(request.getShippingFee()))
                .discountAmount(defaultMoney(request.getDiscountAmount()))
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CreateOrderItemRequest itemRequest : request.getItems()) {
            ProductVariant variant = productVariantRepository.findById(itemRequest.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy variant"));

            validateStock(variant, itemRequest.getQuantity());

            BigDecimal price = resolvePrice(variant);
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .variant(variant)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(price)
                    .lineTotal(lineTotal)
                    .build();

            order.addItem(orderItem);
            subtotal = subtotal.add(lineTotal);
            decreaseStock(variant, itemRequest.getQuantity());
        }

        order.setSubtotal(subtotal);
        order.setTotal(subtotal.add(order.getShippingFee()).subtract(order.getDiscountAmount()));

        Order savedOrder = orderRepository.save(order);
        if (staff != null) {
            auditLogService.log(staff, "CREATE_ORDER", "ORDER", savedOrder.getId(),
                    "Tạo đơn hàng mới, tổng tiền = " + savedOrder.getTotal());
        }

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional
    public OrderResponse createOrderFromCart(CreateCheckoutRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new RuntimeException("Giỏ hàng của khách đang trống"));
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Không thể checkout vì giỏ hàng đang trống");
        }

        CustomerAddress address = resolveCheckoutAddress(customer.getId(), request.getAddressId());

        Order order = Order.builder()
                .customer(customer)
                .code(generateOrderCode())
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getReceiverPhone())
                .shippingAddress(buildFullAddress(address))
                .note(trimToNull(request.getNote()))
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.MOI)
                .paymentMethod(parsePaymentMethod(request.getPaymentMethod()))
                .paymentStatus("UNPAID")
                .channel(OrderChannel.ONLINE)
                .subtotal(BigDecimal.ZERO)
                .shippingFee(defaultMoney(request.getShippingFee()))
                .discountAmount(defaultMoney(request.getDiscountAmount()))
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = productVariantRepository.findById(cartItem.getVariant().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy variant trong giỏ hàng"));

            validateStock(variant, cartItem.getQuantity());
            BigDecimal price = resolvePrice(variant);
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .variant(variant)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(price)
                    .lineTotal(lineTotal)
                    .build();

            order.addItem(orderItem);
            subtotal = subtotal.add(lineTotal);
            decreaseStock(variant, cartItem.getQuantity());
        }

        order.setSubtotal(subtotal);
        order.setTotal(subtotal.add(order.getShippingFee()).subtract(order.getDiscountAmount()));

        Order saved = orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);
        return orderMapper.toResponse(saved);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(orderMapper::toResponse).toList();
    }

    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream().map(orderMapper::toResponse).toList();
    }

    public List<OrderResponse> getOrdersByCustomer(Integer customerId) {
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId).stream().map(orderMapper::toResponse).toList();
    }

    public List<OrderResponse> getOrdersByChannel(String channel) {
        return orderRepository.findByChannel(OrderChannel.valueOf(channel.toUpperCase())).stream().map(orderMapper::toResponse).toList();
    }

    public List<OrderResponse> getOrdersByDate(LocalDate date) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();
        return orderRepository.findByOrderDateBetween(from, to).stream().map(orderMapper::toResponse).toList();
    }

    public OrderResponse getOrderById(Integer id) {
        return orderMapper.toResponse(orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng")));
    }

    @Transactional
    public OrderResponse updateOrderStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        order.setStatus(parseStatus(status));
        Order saved = orderRepository.save(order);
        if (order.getStaff() != null) {
            auditLogService.log(order.getStaff(), "UPDATE_ORDER_STATUS", "ORDER", saved.getId(),
                    "Cập nhật trạng thái đơn thành " + saved.getStatus().name());
        }
        return orderMapper.toResponse(saved);
    }

    @Transactional
    public OrderResponse updateOrderItem(Integer orderId, Integer itemId, UpdateOrderItemRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy order item"));
        if (!item.getOrder().getId().equals(orderId)) throw new RuntimeException("Order item không thuộc đơn hàng này");
        int oldQty = item.getQuantity();
        int newQty = request.getQuantity();
        if (newQty <= 0) throw new RuntimeException("Số lượng phải lớn hơn 0");

        int diff = newQty - oldQty;
        ProductVariant variant = item.getVariant();
        if (diff > 0 && variant.getStockQuantity() < diff) throw new RuntimeException("Không đủ tồn kho để cập nhật");

        int newStock = variant.getStockQuantity() - diff;
        variant.setStockQuantity(newStock);
        variant.setStockStatus(newStock > 0 ? VariantStockStatus.CON_HANG : VariantStockStatus.HET_HANG);
        productVariantRepository.save(variant);

        item.setQuantity(newQty);
        item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(newQty)));
        orderItemRepository.save(item);

        recalculateOrderTotal(order);
        Order saved = orderRepository.save(order);
        if (order.getStaff() != null) {
            auditLogService.log(order.getStaff(), "UPDATE_ORDER_ITEM", "ORDER", saved.getId(),
                    "Cập nhật item " + itemId + " từ " + oldQty + " thành " + newQty);
        }
        return orderMapper.toResponse(saved);
    }

    @Transactional
    public OrderResponse cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        if (order.getStatus() == OrderStatus.HUY) throw new RuntimeException("Đơn hàng đã hủy trước đó");
        if (order.getStatus() == OrderStatus.DANG_GIAO || order.getStatus() == OrderStatus.HOAN_TAT) {
            throw new RuntimeException("Không thể hủy đơn khi đơn đang giao hoặc đã hoàn tất");
        }

        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getVariant();
            int newStock = variant.getStockQuantity() + item.getQuantity();
            variant.setStockQuantity(newStock);
            variant.setStockStatus(newStock > 0 ? VariantStockStatus.CON_HANG : VariantStockStatus.HET_HANG);
            productVariantRepository.save(variant);
        }
        order.setStatus(OrderStatus.HUY);
        Order saved = orderRepository.save(order);
        if (order.getStaff() != null) {
            auditLogService.log(order.getStaff(), "CANCEL_ORDER", "ORDER", order.getId(), "Hủy đơn hàng và hoàn lại tồn kho");
        }
        return orderMapper.toResponse(saved);
    }

    private void recalculateOrderTotal(Order order) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setLineTotal(lineTotal);
            subtotal = subtotal.add(lineTotal);
        }
        order.setSubtotal(subtotal);
        order.setTotal(subtotal.add(order.getShippingFee() == null ? BigDecimal.ZERO : order.getShippingFee())
                .subtract(order.getDiscountAmount() == null ? BigDecimal.ZERO : order.getDiscountAmount()));
    }

    private CustomerAddress resolveCheckoutAddress(Integer customerId, Integer addressId) {
        if (addressId != null) {
            return customerAddressRepository.findByIdAndCustomerId(addressId, customerId)
                    .orElseThrow(() -> new RuntimeException("Địa chỉ không thuộc khách hàng này"));
        }
        return customerAddressRepository.findFirstByCustomerIdAndIsDefaultTrueOrderByIdDesc(customerId)
                .orElseGet(() -> customerAddressRepository.findByCustomerIdOrderByIdDesc(customerId).stream()
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Khách hàng chưa có địa chỉ để checkout")));
    }

    private String buildFullAddress(CustomerAddress address) {
        StringBuilder sb = new StringBuilder();
        appendAddressPart(sb, address.getDetailAddress());
        appendAddressPart(sb, address.getWard());
        appendAddressPart(sb, address.getDistrict());
        appendAddressPart(sb, address.getProvince());
        return sb.toString();
    }

    private void appendAddressPart(StringBuilder sb, String value) {
        if (value == null || value.isBlank()) return;
        if (!sb.isEmpty()) sb.append(", ");
        sb.append(value.trim());
    }

    private void validateStock(ProductVariant variant, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }
        if (variant.getStockQuantity() == null || variant.getStockQuantity() < quantity) {
            throw new RuntimeException("Không đủ tồn kho cho SKU: " + variant.getSku());
        }
    }

    private void decreaseStock(ProductVariant variant, int quantity) {
        int newStock = variant.getStockQuantity() - quantity;
        variant.setStockQuantity(newStock);
        variant.setStockStatus(newStock > 0 ? VariantStockStatus.CON_HANG : VariantStockStatus.HET_HANG);
        productVariantRepository.save(variant);
    }

    private BigDecimal resolvePrice(ProductVariant variant) {
        return variant.getSalePrice() != null ? variant.getSalePrice() : variant.getPrice();
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String generateOrderCode() {
        return "ORD" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private PaymentMethod parsePaymentMethod(String value) {
        return value == null ? PaymentMethod.COD : PaymentMethod.valueOf(value.toUpperCase());
    }

    private OrderChannel parseChannel(String value) {
        return value == null ? OrderChannel.ONLINE : OrderChannel.valueOf(value.toUpperCase());
    }

    private OrderStatus parseStatus(String value) { return OrderStatus.valueOf(value.toUpperCase()); }
}
