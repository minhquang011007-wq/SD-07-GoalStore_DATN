package com.example.demo.order.service.impl;

import com.example.demo.common.exception.BadRequestException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.order.dto.CreateOrderRequest;
import com.example.demo.order.dto.OrderItemRequest;
import com.example.demo.order.dto.OrderItemResponse;
import com.example.demo.order.dto.OrderResponse;
import com.example.demo.order.dto.UpdateOrderItemRequest;
import com.example.demo.order.entity.Order;
import com.example.demo.order.repository.OrderRepository;
import com.example.demo.order.service.OrderService;
import com.example.demo.order_item.entity.OrderItem;
import com.example.demo.order_item.repository.OrderItemRepository;
import com.example.demo.product_category.variant.entity.ProductVariant;
import com.example.demo.product_category.variant.repository.ProductVariantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductVariantRepository productVariantRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            CustomerRepository customerRepository,
                            ProductVariantRepository productVariantRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request.getCustomerId() == null) {
            throw new BadRequestException("customerId không được để trống");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Đơn hàng phải có ít nhất 1 sản phẩm");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("MOI");
        order.setPaymentMethod(request.getPaymentMethod());
        order.setChannel(request.getChannel());
        order.setTotal(BigDecimal.ZERO);

        order = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> savedItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            ProductVariant variant = productVariantRepository.findById(itemRequest.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy biến thể sản phẩm id=" + itemRequest.getVariantId()
                    ));

            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new BadRequestException("Số lượng phải > 0");
            }

            if (variant.getTonKho() < itemRequest.getQuantity()) {
                throw new BadRequestException("Sản phẩm SKU " + variant.getSku() + " không đủ tồn kho");
            }

            BigDecimal price = variant.getGiaKhuyenMai() != null
                    ? variant.getGiaKhuyenMai()
                    : variant.getGiaBan();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariant(variant);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(price);

            savedItems.add(orderItem);

            variant.setTonKho(variant.getTonKho() - itemRequest.getQuantity());

            total = total.add(price.multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        orderItemRepository.saveAll(savedItems);
        order.setTotal(total);
        orderRepository.save(order);

        return mapToResponse(order, savedItems);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> responses = new ArrayList<>();

        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            responses.add(mapToResponse(order, items));
        }

        return responses;
    }

    @Override
    public OrderResponse getOrderById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng id=" + id));

        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        return mapToResponse(order, items);
    }

    @Override
    public OrderResponse updateStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng id=" + orderId));

        order.setStatus(status);
        orderRepository.save(order);

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return mapToResponse(order, items);
    }

    @Override
    public OrderResponse addItem(Integer orderId, Integer variantId, Integer quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng id=" + orderId));

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm"));

        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Số lượng phải > 0");
        }

        if (variant.getTonKho() < quantity) {
            throw new BadRequestException("Không đủ tồn kho");
        }

        BigDecimal price = variant.getGiaKhuyenMai() != null
                ? variant.getGiaKhuyenMai()
                : variant.getGiaBan();

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setVariant(variant);
        item.setQuantity(quantity);
        item.setUnitPrice(price);

        orderItemRepository.save(item);

        variant.setTonKho(variant.getTonKho() - quantity);

        recalculateOrderTotal(order);

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return mapToResponse(order, items);
    }

    @Override
    public OrderResponse updateItem(Integer orderId, Integer orderItemId, UpdateOrderItemRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy order item"));

        if (!item.getOrder().getId().equals(orderId)) {
            throw new BadRequestException("Item không thuộc đơn hàng này");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestException("Số lượng phải > 0");
        }

        int oldQty = item.getQuantity();
        int newQty = request.getQuantity();
        int diff = newQty - oldQty;

        ProductVariant variant = item.getVariant();

        if (diff > 0 && variant.getTonKho() < diff) {
            throw new BadRequestException("Không đủ tồn kho để tăng số lượng");
        }

        variant.setTonKho(variant.getTonKho() - diff);
        item.setQuantity(newQty);

        orderItemRepository.save(item);
        recalculateOrderTotal(order);

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return mapToResponse(order, items);
    }

    @Override
    public void deleteItem(Integer orderId, Integer orderItemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy order item"));

        if (!item.getOrder().getId().equals(orderId)) {
            throw new BadRequestException("Item không thuộc đơn hàng này");
        }

        ProductVariant variant = item.getVariant();
        variant.setTonKho(variant.getTonKho() + item.getQuantity());

        orderItemRepository.delete(item);
        recalculateOrderTotal(order);
    }

    private void recalculateOrderTotal(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : items) {
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(lineTotal);
        }

        order.setTotal(total);
        orderRepository.save(order);
    }

    private OrderResponse mapToResponse(Order order, List<OrderItem> items) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomer() != null ? order.getCustomer().getId() : null);
        response.setCustomerName(order.getCustomer() != null ? order.getCustomer().getTen() : null);
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setChannel(order.getChannel());
        response.setTotal(order.getTotal());

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : items) {
            OrderItemResponse ir = new OrderItemResponse();
            ir.setId(item.getId());
            ir.setVariantId(item.getVariant().getId());
            ir.setSku(item.getVariant().getSku());
            ir.setProductName(item.getVariant().getProduct().getTenSanPham());
            ir.setSize(item.getVariant().getSize());
            ir.setColor(item.getVariant().getMauSac());
            ir.setQuantity(item.getQuantity());
            ir.setUnitPrice(item.getUnitPrice());
            ir.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            itemResponses.add(ir);
        }

        response.setItems(itemResponses);
        return response;
    }
}