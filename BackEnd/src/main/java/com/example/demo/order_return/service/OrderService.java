package com.example.demo.order_return.service;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.order_return.dto.CreateOrderItemRequest;
import com.example.demo.order_return.dto.CreateOrderRequest;
import com.example.demo.order_return.dto.OrderResponse;
import com.example.demo.order_return.dto.UpdateOrderItemRequest;

import com.example.demo.order_return.entity.Order;
import com.example.demo.order_return.entity.OrderChannel;
import com.example.demo.order_return.entity.OrderItem;
import com.example.demo.order_return.entity.OrderStatus;
import com.example.demo.order_return.entity.PaymentMethod;
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

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
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
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.MOI)
                .paymentMethod(parsePaymentMethod(request.getPaymentMethod()))
                .channel(parseChannel(request.getChannel()))
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemRequest : request.getItems()) {
            ProductVariant variant = productVariantRepository.findById(itemRequest.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy variant"));

            if (variant.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Không đủ tồn kho cho SKU: " + variant.getSku());
            }

            BigDecimal price = variant.getSalePrice() != null
                    ? variant.getSalePrice()
                    : variant.getPrice();

            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .variant(variant)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(price)
                    .lineTotal(lineTotal)
                    .build();

            order.addItem(orderItem);
            total = total.add(lineTotal);

            int newStock = variant.getStockQuantity() - itemRequest.getQuantity();
            variant.setStockQuantity(newStock);
            variant.setStockStatus(newStock > 0
                    ? VariantStockStatus.CON_HANG
                    : VariantStockStatus.HET_HANG);

            productVariantRepository.save(variant);
        }

        order.setTotal(total);
        Order savedOrder = orderRepository.save(order);

        if (staff != null) {
            auditLogService.log(
                    staff,
                    "CREATE_ORDER",
                    "ORDER",
                    savedOrder.getId(),
                    "Tạo đơn hàng mới, tổng tiền = " + savedOrder.getTotal()
            );
        }

        return orderMapper.toResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public List<OrderResponse> getOrdersByCustomer(Integer customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public List<OrderResponse> getOrdersByChannel(String channel) {
        OrderChannel orderChannel = OrderChannel.valueOf(channel.toUpperCase());
        return orderRepository.findByChannel(orderChannel)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public List<OrderResponse> getOrdersByDate(LocalDate date) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();

        return orderRepository.findByOrderDateBetween(from, to)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public OrderResponse getOrderById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return orderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        order.setStatus(parseStatus(status));
        Order saved = orderRepository.save(order);

        if (order.getStaff() != null) {
            auditLogService.log(
                    order.getStaff(),
                    "UPDATE_ORDER_STATUS",
                    "ORDER",
                    saved.getId(),
                    "Cập nhật trạng thái đơn thành " + saved.getStatus().name()
            );
        }

        return orderMapper.toResponse(saved);
    }

    @Transactional
    public OrderResponse updateOrderItem(Integer orderId, Integer itemId, UpdateOrderItemRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy order item"));

        if (!item.getOrder().getId().equals(orderId)) {
            throw new RuntimeException("Order item không thuộc đơn hàng này");
        }

        int oldQty = item.getQuantity();
        int newQty = request.getQuantity();

        if (newQty <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        int diff = newQty - oldQty;
        ProductVariant variant = item.getVariant();

        if (diff > 0 && variant.getStockQuantity() < diff) {
            throw new RuntimeException("Không đủ tồn kho để cập nhật");
        }

        int newStock = variant.getStockQuantity() - diff;
        variant.setStockQuantity(newStock);
        variant.setStockStatus(newStock > 0
                ? VariantStockStatus.CON_HANG
                : VariantStockStatus.HET_HANG);

        productVariantRepository.save(variant);

        item.setQuantity(newQty);
        item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(newQty)));
        orderItemRepository.save(item);

        recalculateOrderTotal(order);
        Order saved = orderRepository.save(order);

        if (order.getStaff() != null) {
            auditLogService.log(
                    order.getStaff(),
                    "UPDATE_ORDER_ITEM",
                    "ORDER",
                    saved.getId(),
                    "Cập nhật item " + itemId + " từ " + oldQty + " thành " + newQty
            );
        }

        return orderMapper.toResponse(saved);
    }

    @Transactional
    public void cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (order.getStatus() == OrderStatus.HUY) {
            throw new RuntimeException("Đơn hàng đã hủy trước đó");
        }

        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getVariant();

            int newStock = variant.getStockQuantity() + item.getQuantity();
            variant.setStockQuantity(newStock);
            variant.setStockStatus(newStock > 0
                    ? VariantStockStatus.CON_HANG
                    : VariantStockStatus.HET_HANG);

            productVariantRepository.save(variant);
        }

        order.setStatus(OrderStatus.HUY);
        orderRepository.save(order);

        if (order.getStaff() != null) {
            auditLogService.log(
                    order.getStaff(),
                    "CANCEL_ORDER",
                    "ORDER",
                    order.getId(),
                    "Hủy đơn hàng và hoàn lại tồn kho"
            );
        }
    }

    private void recalculateOrderTotal(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setLineTotal(lineTotal);
            total = total.add(lineTotal);
        }
        order.setTotal(total);
    }

    private PaymentMethod parsePaymentMethod(String value) {
        return value == null ? null : PaymentMethod.valueOf(value.toUpperCase());
    }

    private OrderChannel parseChannel(String value) {
        return value == null ? OrderChannel.ONLINE : OrderChannel.valueOf(value.toUpperCase());
    }

    private OrderStatus parseStatus(String value) {
        return OrderStatus.valueOf(value.toUpperCase());
    }
}