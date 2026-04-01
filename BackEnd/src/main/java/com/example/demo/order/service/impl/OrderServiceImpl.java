package com.example.demo.order.service.impl;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.loyalty.entity.LoyaltyPointHistory;
import com.example.demo.loyalty.entity.VipHistory;
import com.example.demo.loyalty.entity.VipProgram;
import com.example.demo.loyalty.repository.LoyaltyRepository;
import com.example.demo.loyalty.repository.VipHistoryRepository;
import com.example.demo.loyalty.repository.VipProgramRepository;
import com.example.demo.order.dto.*;
import com.example.demo.order.entity.Order;
import com.example.demo.order.entity.OrderItem;
import com.example.demo.order.entity.ReturnOrder;
import com.example.demo.order.repository.OrderItemRepository;
import com.example.demo.order.repository.OrderRepository;
import com.example.demo.order.repository.ReturnOrderRepository;
import com.example.demo.order.service.OrderService;
import com.example.demo.product_category.common.enums.VariantStockStatus;
import com.example.demo.product_category.product.entity.Product;
import com.example.demo.product_category.variant.entity.ProductVariant;
import com.example.demo.product_category.variant.repository.ProductVariantRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReturnOrderRepository returnOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductVariantRepository productVariantRepository;
    private final LoyaltyRepository loyaltyRepository;
    private final VipProgramRepository vipProgramRepository;
    private final VipHistoryRepository vipHistoryRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ReturnOrderRepository returnOrderRepository,
            CustomerRepository customerRepository,
            ProductVariantRepository productVariantRepository,
            LoyaltyRepository loyaltyRepository,
            VipProgramRepository vipProgramRepository,
            VipHistoryRepository vipHistoryRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.returnOrderRepository = returnOrderRepository;
        this.customerRepository = customerRepository;
        this.productVariantRepository = productVariantRepository;
        this.loyaltyRepository = loyaltyRepository;
        this.vipProgramRepository = vipProgramRepository;
        this.vipHistoryRepository = vipHistoryRepository;
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllWithCustomerOrderByOrderDateDesc()
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(String status) {
        if (status == null || status.isBlank()) {
            return getAllOrders();
        }
        return orderRepository.findByStatusWithCustomerOrderByOrderDateDesc(normalizeStatus(status))
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    public List<OrderResponse> getOrdersByCustomer(Integer customerId) {
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    public OrderDetailResponse getOrderDetail(Integer id) {
        return toDetail(findOrder(id));
    }

    @Override
    public OrderDetailResponse createOrder(CreateOrderRequest request) {
        validateCreateRequest(request);

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("MOI");
        order.setPaymentMethod(safeUpper(request.getPaymentMethod()));
        order.setChannel(safeUpper(request.getChannel()));
        order.setTotal(BigDecimal.ZERO);

        order = orderRepository.save(order);

        BigDecimal total = rebuildOrderItems(order.getId(), request.getItems(), false);
        order.setTotal(total);
        orderRepository.save(order);

        return toDetail(order);
    }

    @Override
    public OrderDetailResponse updateOrder(Integer id, UpdateOrderRequest request) {
        Order order = findOrder(id);

        ensureEditable(order);

        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Đơn hàng phải có ít nhất 1 sản phẩm");
        }

        restoreStockForOrder(order.getId());

        orderItemRepository.deleteByOrderId(order.getId());

        BigDecimal total = rebuildOrderItems(order.getId(), request.getItems(), false);

        if (request.getPaymentMethod() != null) {
            order.setPaymentMethod(safeUpper(request.getPaymentMethod()));
        }

        if (request.getChannel() != null) {
            order.setChannel(safeUpper(request.getChannel()));
        }

        order.setTotal(total);
        orderRepository.save(order);

        return toDetail(order);
    }

    @Override
    public OrderDetailResponse updateStatus(Integer id, UpdateOrderStatusRequest request) {
        Order order = findOrder(id);

        if (request == null || request.getStatus() == null || request.getStatus().isBlank()) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }

        String oldStatus = safeUpper(order.getStatus());
        String newStatus = normalizeStatus(request.getStatus());

        if (Objects.equals(oldStatus, newStatus)) {
            return toDetail(order);
        }

        if ("HUY".equals(oldStatus)) {
            throw new RuntimeException("Đơn đã hủy, không thể cập nhật trạng thái");
        }

        if ("TRA_HANG".equals(oldStatus)) {
            throw new RuntimeException("Đơn đã trả hàng, không thể cập nhật trạng thái");
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        if (!"HOAN_TAT".equals(oldStatus) && "HOAN_TAT".equals(newStatus)) {
            int pointsToAdd = calculatePoints(order.getTotal());
            addLoyalty(order.getCustomer(), pointsToAdd, "ADD", "Cộng điểm từ đơn hàng #" + order.getId());
            recalculateVip(order.getCustomer(), "Đơn hàng #" + order.getId() + " hoàn tất");
        }

        return toDetail(order);
    }

    @Override
    public OrderDetailResponse cancelOrder(Integer id) {
        Order order = findOrder(id);

        if ("HUY".equalsIgnoreCase(order.getStatus())) {
            return toDetail(order);
        }

        if ("TRA_HANG".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Đơn đã trả hàng, không thể hủy");
        }

        if ("HOAN_TAT".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Đơn đã hoàn tất, muốn hoàn lại phải dùng trả hàng");
        }

        restoreStockForOrder(order.getId());
        order.setStatus("HUY");
        orderRepository.save(order);

        return toDetail(order);
    }

    @Override
    public OrderDetailResponse returnOrder(Integer id, ReturnOrderRequest request) {
        Order order = findOrder(id);

        if (returnOrderRepository.existsByOrderId(id)) {
            throw new RuntimeException("Đơn hàng này đã được trả");
        }

        if ("HUY".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Đơn đã hủy, không thể trả");
        }

        if (!"HOAN_TAT".equalsIgnoreCase(order.getStatus())
                && !"DANG_GIAO".equalsIgnoreCase(order.getStatus())
                && !"DANG_XU_LY".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Chỉ cho phép trả đơn đang xử lý, đang giao hoặc hoàn tất");
        }

        restoreStockForOrder(order.getId());

        BigDecimal refundTotal = request != null && request.getRefundTotal() != null
                ? request.getRefundTotal()
                : order.getTotal();

        if (refundTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Số tiền hoàn không hợp lệ");
        }

        ReturnOrder returnOrder = new ReturnOrder();
        returnOrder.setOrderId(order.getId());
        returnOrder.setReturnDate(LocalDateTime.now());
        returnOrder.setReason(request != null ? request.getReason() : null);
        returnOrder.setRefundTotal(refundTotal);
        returnOrder.setNote(request != null ? request.getNote() : null);
        returnOrderRepository.save(returnOrder);

        boolean wasCompleted = "HOAN_TAT".equalsIgnoreCase(order.getStatus());

        order.setStatus("TRA_HANG");
        order.setTotal(order.getTotal().subtract(refundTotal));
        if (order.getTotal().compareTo(BigDecimal.ZERO) < 0) {
            order.setTotal(BigDecimal.ZERO);
        }
        orderRepository.save(order);

        if (wasCompleted) {
            int pointsToSubtract = calculatePoints(refundTotal);
            subtractLoyalty(order.getCustomer(), pointsToSubtract, "SUBTRACT", "Trừ điểm do trả hàng đơn #" + order.getId());
            recalculateVip(order.getCustomer(), "Trả hàng đơn #" + order.getId());
        }

        return toDetail(order);
    }

    @Override
    public void deleteOrder(Integer id) {
        Order order = findOrder(id);

        if ("HOAN_TAT".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Không được xóa đơn đã hoàn tất");
        }

        if (returnOrderRepository.existsByOrderId(id)) {
            throw new RuntimeException("Không thể xóa đơn đã phát sinh trả hàng");
        }

        restoreStockForOrder(order.getId());
        orderItemRepository.deleteByOrderId(order.getId());

        try {
            orderRepository.delete(order);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Không thể xóa đơn hàng");
        }
    }

    private void validateCreateRequest(CreateOrderRequest request) {
        if (request == null) {
            throw new RuntimeException("Request không hợp lệ");
        }
        if (request.getCustomerId() == null) {
            throw new RuntimeException("Thiếu customerId");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Đơn hàng phải có ít nhất 1 sản phẩm");
        }
    }

    private void ensureEditable(Order order) {
        String status = safeUpper(order.getStatus());
        if ("HOAN_TAT".equals(status) || "HUY".equals(status) || "TRA_HANG".equals(status)) {
            throw new RuntimeException("Đơn ở trạng thái hiện tại không được chỉnh sửa");
        }
    }

    private Order findOrder(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
    }

    private String normalizeStatus(String status) {
        String value = safeUpper(status);
        List<String> valid = List.of("MOI", "DANG_XU_LY", "DANG_GIAO", "HOAN_TAT", "HUY", "TRA_HANG");
        if (!valid.contains(value)) {
            throw new RuntimeException("Trạng thái đơn không hợp lệ");
        }
        return value;
    }

    private BigDecimal rebuildOrderItems(Integer orderId, List<CreateOrderItemRequest> items, boolean restoreStockBefore) {
        if (restoreStockBefore) {
            restoreStockForOrder(orderId);
        }

        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemRequest : items) {
            if (itemRequest.getVariantId() == null) {
                throw new RuntimeException("Thiếu variantId");
            }
            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new RuntimeException("Số lượng phải lớn hơn 0");
            }

            ProductVariant variant = productVariantRepository.findById(itemRequest.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy variant " + itemRequest.getVariantId()));

            if (variant.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Tồn kho không đủ cho SKU: " + variant.getSku());
            }

            BigDecimal price = variant.getSalePrice() != null ? variant.getSalePrice() : variant.getPrice();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setVariantId(variant.getId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(price);
            orderItemRepository.save(orderItem);

            variant.setStockQuantity(variant.getStockQuantity() - itemRequest.getQuantity());
            variant.setStockStatus(variant.getStockQuantity() > 0
                    ? VariantStockStatus.CON_HANG
                    : VariantStockStatus.HET_HANG);
            productVariantRepository.save(variant);

            total = total.add(price.multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        return total;
    }

    private void restoreStockForOrder(Integer orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        for (OrderItem item : items) {
            ProductVariant variant = productVariantRepository.findById(item.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy variant " + item.getVariantId()));

            int newStock = variant.getStockQuantity() + item.getQuantity();
            variant.setStockQuantity(newStock);
            variant.setStockStatus(newStock > 0 ? VariantStockStatus.CON_HANG : VariantStockStatus.HET_HANG);
            productVariantRepository.save(variant);
        }
    }

    private int calculatePoints(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return amount.divide(new BigDecimal("10000"), RoundingMode.DOWN).intValue();
    }

    private void addLoyalty(Customer customer, int points, String type, String note) {
        if (points <= 0) {
            return;
        }

        customer.setDiemTichLuy((customer.getDiemTichLuy() == null ? 0 : customer.getDiemTichLuy()) + points);
        customerRepository.save(customer);

        LoyaltyPointHistory history = new LoyaltyPointHistory();
        history.setCustomerId(customer.getId());
        history.setPoints(points);
        history.setType(type);
        history.setNote(note);
        history.setCreatedAt(LocalDateTime.now());
        loyaltyRepository.save(history);
    }

    private void subtractLoyalty(Customer customer, int points, String type, String note) {
        if (points <= 0) {
            return;
        }

        int current = customer.getDiemTichLuy() == null ? 0 : customer.getDiemTichLuy();
        int newValue = Math.max(0, current - points);
        customer.setDiemTichLuy(newValue);
        customerRepository.save(customer);

        LoyaltyPointHistory history = new LoyaltyPointHistory();
        history.setCustomerId(customer.getId());
        history.setPoints(points);
        history.setType(type);
        history.setNote(note);
        history.setCreatedAt(LocalDateTime.now());
        loyaltyRepository.save(history);
    }

    private void recalculateVip(Customer customer, String reason) {
        List<VipProgram> programs = vipProgramRepository.findByIsActiveTrueOrderByMinPointsAsc();
        if (programs.isEmpty()) {
            return;
        }

        BigDecimal spending = orderRepository.sumValidSpending(customer.getId());
        int points = customer.getDiemTichLuy() == null ? 0 : customer.getDiemTichLuy();

        String oldLevel = customer.getLoaiKhach();
        String matchedLevel = "THUONG";

        for (VipProgram program : programs) {
            boolean enoughPoints = points >= (program.getMinPoints() == null ? 0 : program.getMinPoints());
            boolean enoughSpending = spending.compareTo(program.getMinSpending() == null ? BigDecimal.ZERO : program.getMinSpending()) >= 0;
            if (enoughPoints || enoughSpending) {
                matchedLevel = program.getLevelName();
            }
        }

        if (!Objects.equals(oldLevel, matchedLevel)) {
            customer.setLoaiKhach(matchedLevel);
            customerRepository.save(customer);

            VipHistory vipHistory = new VipHistory();
            vipHistory.setCustomerId(customer.getId());
            vipHistory.setOldLevel(oldLevel);
            vipHistory.setNewLevel(matchedLevel);
            vipHistory.setReason(reason);
            vipHistory.setChangedAt(LocalDateTime.now());
            vipHistoryRepository.save(vipHistory);
        }
    }

    private OrderResponse toSummary(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomer().getId());
        response.setCustomerName(order.getCustomer().getTen());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setChannel(order.getChannel());
        response.setTotal(order.getTotal());
        response.setTotalItems(items.stream().mapToInt(OrderItem::getQuantity).sum());
        return response;
    }

    private OrderDetailResponse toDetail(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemDetailResponse> detailItems = new ArrayList<>();

        for (OrderItem item : items) {
            ProductVariant variant = productVariantRepository.findById(item.getVariantId()).orElse(null);

            OrderItemDetailResponse r = new OrderItemDetailResponse();
            r.setId(item.getId());
            r.setVariantId(item.getVariantId());
            r.setQuantity(item.getQuantity());
            r.setUnitPrice(item.getUnitPrice());
            r.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            if (variant != null) {
                r.setVariantSku(variant.getSku());
                r.setSize(variant.getSize());
                r.setColor(variant.getColor());

                Product product = variant.getProduct();
                if (product != null) {
                    r.setProductName(product.getName());
                }
            }

            detailItems.add(r);
        }

        ReturnResponse returnResponse = returnOrderRepository.findByOrderId(order.getId())
                .map(ret -> {
                    ReturnResponse rr = new ReturnResponse();
                    rr.setId(ret.getId());
                    rr.setOrderId(ret.getOrderId());
                    rr.setReturnDate(ret.getReturnDate());
                    rr.setReason(ret.getReason());
                    rr.setRefundTotal(ret.getRefundTotal());
                    rr.setNote(ret.getNote());
                    return rr;
                })
                .orElse(null);

        OrderDetailResponse response = new OrderDetailResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomer().getId());
        response.setCustomerName(order.getCustomer().getTen());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setChannel(order.getChannel());
        response.setTotal(order.getTotal());
        response.setItems(detailItems);
        response.setTotalItems(detailItems.stream().mapToInt(OrderItemDetailResponse::getQuantity).sum());
        response.setReturnInfo(returnResponse);

        return response;
    }

    private String safeUpper(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}