package com.example.demo.customer.service.impl;

import com.example.demo.customer.dto.*;
import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.customer.service.CustomerService;
import com.example.demo.loyalty.repository.LoyaltyRepository;
import com.example.demo.order.entity.Order;
import com.example.demo.order.entity.OrderItem;
import com.example.demo.order.repository.OrderItemRepository;
import com.example.demo.order.repository.OrderRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final LoyaltyRepository loyaltyRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               OrderRepository orderRepository,
                               OrderItemRepository orderItemRepository,
                               LoyaltyRepository loyaltyRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.loyaltyRepository = loyaltyRepository;
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.getCustomerSummaryRows()
                .stream()
                .map(this::mapSummaryRowToCustomerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerStatsResponse getCustomerStats() {
        List<CustomerResponse> customers = getAllCustomers();

        int totalCustomers = customers.size();
        int totalVipCustomers = (int) customers.stream()
                .filter(c -> "VIP".equalsIgnoreCase(c.getLoaiKhach()))
                .count();

        int totalRegularCustomers = totalCustomers - totalVipCustomers;

        int totalPoints = customers.stream()
                .map(CustomerResponse::getDiemTichLuy)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);

        BigDecimal totalRevenue = customers.stream()
                .map(CustomerResponse::getTongChiTieu)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int active30Days = (int) customers.stream()
                .filter(c -> c.getSoNgayKhongMua() != null && c.getSoNgayKhongMua() <= 30)
                .count();

        CustomerStatsResponse response = new CustomerStatsResponse();
        response.setTotalCustomers(totalCustomers);
        response.setTotalVipCustomers(totalVipCustomers);
        response.setTotalRegularCustomers(totalRegularCustomers);
        response.setTotalPoints(totalPoints);
        response.setTotalRevenueFromCustomers(totalRevenue);
        response.setActive30Days(active30Days);

        return response;
    }

    @Override
    public List<CustomerActivityResponse> getCustomerActivity(Integer days) {
        int validDays = (days == null || days <= 0) ? 30 : days;

        return customerRepository.getCustomerActivityRows(validDays)
                .stream()
                .map(this::mapActivityRow)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponse> filterCustomers(CustomerFilterRequest request) {
        List<CustomerResponse> customers = getAllCustomers();

        if (request == null) {
            return customers;
        }

        return customers.stream()
                .filter(c -> matchKeyword(c, request.getKeyword()))
                .filter(c -> matchLoaiKhach(c, request.getLoaiKhach()))
                .filter(c -> matchPoints(c, request.getMinPoints(), request.getMaxPoints()))
                .filter(c -> matchSpending(c, request.getMinSpending(), request.getMaxSpending()))
                .filter(c -> matchInactiveDays(c, request.getInactiveDays()))
                .sorted(buildComparator(request.getSortBy()))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        return enrichBaseCustomer(customer);
    }

    @Override
    public CustomerDetailResponse getCustomerDetail(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        List<CustomerOrderHistoryResponse> orders = getCustomerOrderHistory(id);

        Integer totalOrders = orders.size();
        BigDecimal totalSpending = orders.stream()
                .map(CustomerOrderHistoryResponse::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime lastOrderDate = orders.stream()
                .map(CustomerOrderHistoryResponse::getOrderDate)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        CustomerDetailResponse response = new CustomerDetailResponse();
        response.setId(customer.getId());
        response.setTen(customer.getTen());
        response.setEmail(customer.getEmail());
        response.setSdt(customer.getSdt());
        response.setNgaySinh(customer.getNgaySinh());
        response.setLoaiKhach(customer.getLoaiKhach());
        response.setDiemTichLuy(customer.getDiemTichLuy());
        response.setGhiChu(customer.getGhiChu());
        response.setCreatedAt(customer.getCreatedAt());
        response.setTotalOrders(totalOrders);
        response.setTotalSpending(totalSpending);
        response.setLastOrderDate(lastOrderDate);
        response.setSoNgayKhongMua(calculateDaysSince(lastOrderDate, customer.getCreatedAt()));
        response.setVipLevel(customer.getLoaiKhach());
        response.setOrders(orders);

        return response;
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        validateCustomerRequest(request, null);

        Customer customer = new Customer();
        mapToEntity(request, customer);

        return enrichBaseCustomer(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse updateCustomer(Integer id, CustomerRequest request) {
        validateCustomerRequest(request, id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        mapToEntity(request, customer);

        return enrichBaseCustomer(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse assignVip(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        customer.setLoaiKhach("VIP");

        return enrichBaseCustomer(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        try {
            customerRepository.delete(customer);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Không thể xóa khách hàng vì đã phát sinh đơn hàng");
        }
    }

    @Override
    public List<CustomerResponse> searchCustomerByName(String name) {
        String keyword = safeTrim(name).toLowerCase();

        return getAllCustomers().stream()
                .filter(c -> safe(c.getTen()).toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponse> getCustomersByLoaiKhach(String loaiKhach) {
        String normalized = safeTrim(loaiKhach).toUpperCase();

        return getAllCustomers().stream()
                .filter(c -> safe(c.getLoaiKhach()).equalsIgnoreCase(normalized))
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponse> searchByEmail(String email) {
        String keyword = safeTrim(email).toLowerCase();

        return getAllCustomers().stream()
                .filter(c -> safe(c.getEmail()).toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponse> searchByPhone(String sdt) {
        String keyword = safeTrim(sdt);

        return getAllCustomers().stream()
                .filter(c -> safe(c.getSdt()).contains(keyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponse> searchAll(String keyword) {
        String normalized = safeTrim(keyword).toLowerCase();

        return getAllCustomers().stream()
                .filter(c ->
                        safe(c.getTen()).toLowerCase().contains(normalized)
                                || safe(c.getEmail()).toLowerCase().contains(normalized)
                                || safe(c.getSdt()).contains(normalized)
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerOrderHistoryResponse> getCustomerOrderHistory(Integer customerId) {
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId)
                .stream()
                .map(order -> mapOrderHistory(order, customerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerSpendingResponse> getTopCustomersBySpending() {
        return customerRepository.getTopSpendingRows()
                .stream()
                .map(this::mapSpendingRow)
                .collect(Collectors.toList());
    }

    @Override
    public List<TopLoyalCustomerResponse> getTopCustomersByPoints(Integer limit) {
        int validLimit = (limit == null || limit <= 0) ? 10 : limit;

        return getAllCustomers().stream()
                .sorted(Comparator
                        .comparing((CustomerResponse c) -> c.getDiemTichLuy() == null ? 0 : c.getDiemTichLuy())
                        .reversed()
                        .thenComparing(c -> c.getTongChiTieu() == null ? BigDecimal.ZERO : c.getTongChiTieu(),
                                Comparator.reverseOrder()))
                .limit(validLimit)
                .map(c -> {
                    TopLoyalCustomerResponse r = new TopLoyalCustomerResponse();
                    r.setCustomerId(c.getId());
                    r.setTen(c.getTen());
                    r.setEmail(c.getEmail());
                    r.setSdt(c.getSdt());
                    r.setLoaiKhach(c.getLoaiKhach());
                    r.setDiemTichLuy(c.getDiemTichLuy());
                    r.setTongDonHang(c.getTongDonHang());
                    r.setTongChiTieu(c.getTongChiTieu());
                    r.setLanMuaCuoi(c.getLanMuaCuoi());
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InactiveCustomerResponse> getInactiveCustomers(Long days) {
        long validDays = (days == null || days < 0) ? 30L : days;

        return customerRepository.getInactiveCustomerRows()
                .stream()
                .map(this::mapInactiveRow)
                .filter(item -> item.getSoNgayKhongMua() != null && item.getSoNgayKhongMua() >= validDays)
                .collect(Collectors.toList());
    }

    private CustomerResponse enrichBaseCustomer(Customer customer) {
        CustomerResponse response = mapToBaseResponse(customer);

        Integer totalOrders = orderRepository.countValidOrders(customer.getId());
        BigDecimal totalSpending = orderRepository.sumValidSpending(customer.getId());
        LocalDateTime lastOrderDate = orderRepository.findLastOrderDateByCustomerId(customer.getId());

        response.setTongDonHang(totalOrders == null ? 0 : totalOrders);
        response.setTongChiTieu(totalSpending == null ? BigDecimal.ZERO : totalSpending);
        response.setLanMuaCuoi(lastOrderDate);
        response.setSoNgayKhongMua(calculateDaysSince(lastOrderDate, customer.getCreatedAt()));

        return response;
    }

    private CustomerResponse mapToBaseResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setTen(customer.getTen());
        response.setEmail(customer.getEmail());
        response.setSdt(customer.getSdt());
        response.setNgaySinh(customer.getNgaySinh());
        response.setLoaiKhach(customer.getLoaiKhach());
        response.setDiemTichLuy(customer.getDiemTichLuy());
        response.setGhiChu(customer.getGhiChu());
        response.setCreatedAt(customer.getCreatedAt());
        return response;
    }

    private CustomerResponse mapSummaryRowToCustomerResponse(Object[] row) {
        CustomerResponse response = new CustomerResponse();

        response.setId(toInteger(row[0]));
        response.setTen(toStringValue(row[1]));
        response.setEmail(toStringValue(row[2]));
        response.setSdt(toStringValue(row[3]));
        response.setNgaySinh(toLocalDate(row[4]));
        response.setLoaiKhach(toStringValue(row[5]));
        response.setDiemTichLuy(toInteger(row[6]));
        response.setGhiChu(toStringValue(row[7]));
        response.setCreatedAt(toLocalDateTime(row[8]));
        response.setTongDonHang(toInteger(row[9]));
        response.setTongChiTieu(toBigDecimal(row[10]));
        response.setLanMuaCuoi(toLocalDateTime(row[11]));
        response.setSoNgayKhongMua(calculateDaysSince(response.getLanMuaCuoi(), response.getCreatedAt()));

        return response;
    }

    private CustomerSpendingResponse mapSpendingRow(Object[] row) {
        CustomerSpendingResponse response = new CustomerSpendingResponse();
        response.setCustomerId(toInteger(row[0]));
        response.setTen(toStringValue(row[1]));
        response.setEmail(toStringValue(row[2]));
        response.setSdt(toStringValue(row[3]));
        response.setLoaiKhach(toStringValue(row[4]));
        response.setDiemTichLuy(toInteger(row[5]));
        response.setTongDonHang(toInteger(row[6]));
        response.setTongChiTieu(toBigDecimal(row[7]));
        response.setLanMuaCuoi(toLocalDateTime(row[8]));
        return response;
    }

    private InactiveCustomerResponse mapInactiveRow(Object[] row) {
        InactiveCustomerResponse response = new InactiveCustomerResponse();
        response.setCustomerId(toInteger(row[0]));
        response.setTen(toStringValue(row[1]));
        response.setEmail(toStringValue(row[2]));
        response.setSdt(toStringValue(row[3]));
        response.setLoaiKhach(toStringValue(row[4]));
        response.setTongDonHang(toInteger(row[5]));
        response.setTongChiTieu(toBigDecimal(row[6]));
        response.setLastOrderDate(toLocalDateTime(row[7]));
        response.setSoNgayKhongMua(toLong(row[8]));
        return response;
    }

    private CustomerActivityResponse mapActivityRow(Object[] row) {
        CustomerActivityResponse response = new CustomerActivityResponse();
        response.setCustomerId(toInteger(row[0]));
        response.setTen(toStringValue(row[1]));
        response.setLoaiKhach(toStringValue(row[2]));
        response.setLastOrderDate(toLocalDateTime(row[3]));
        response.setSoDonTrongKy(toInteger(row[4]));
        response.setChiTieuTrongKy(toBigDecimal(row[5]));
        return response;
    }

    private CustomerOrderHistoryResponse mapOrderHistory(Order order, Integer customerId) {
        CustomerOrderHistoryResponse response = new CustomerOrderHistoryResponse();

        Integer added = loyaltyRepository.getPointsAddedByOrder(customerId, order.getId());
        Integer reduced = loyaltyRepository.getPointsReducedByOrder(customerId, order.getId());

        int netPoints = (added == null ? 0 : added) - (reduced == null ? 0 : reduced);

        response.setOrderId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setChannel(order.getChannel());
        response.setTotal(order.getTotal());
        response.setPointsEarned(netPoints);

        List<CustomerOrderItemResponse> items = orderItemRepository.findByOrderId(order.getId())
                .stream()
                .map(this::mapOrderItem)
                .collect(Collectors.toList());

        response.setItems(items);

        return response;
    }

    private CustomerOrderItemResponse mapOrderItem(OrderItem item) {
        CustomerOrderItemResponse response = new CustomerOrderItemResponse();
        response.setId(item.getId());
        response.setVariantId(item.getVariantId());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        return response;
    }

    private void mapToEntity(CustomerRequest request, Customer customer) {
        customer.setTen(safeTrim(request.getTen()));
        customer.setEmail(toNullIfBlank(request.getEmail()));
        customer.setSdt(toNullIfBlank(request.getSdt()));
        customer.setNgaySinh(request.getNgaySinh());
        customer.setGhiChu(toNullIfBlank(request.getGhiChu()));

        if (request.getLoaiKhach() != null && !request.getLoaiKhach().isBlank()) {
            customer.setLoaiKhach(request.getLoaiKhach().trim().toUpperCase());
        } else if (customer.getLoaiKhach() == null) {
            customer.setLoaiKhach("THUONG");
        }

        if (request.getDiemTichLuy() != null) {
            customer.setDiemTichLuy(request.getDiemTichLuy());
        } else if (customer.getDiemTichLuy() == null) {
            customer.setDiemTichLuy(0);
        }
    }

    private void validateCustomerRequest(CustomerRequest request, Integer currentCustomerId) {
        if (request == null) {
            throw new RuntimeException("Dữ liệu khách hàng không hợp lệ");
        }

        if (safeTrim(request.getTen()).isBlank()) {
            throw new RuntimeException("Tên khách hàng không được để trống");
        }

        String email = toNullIfBlank(request.getEmail());

        if (email != null) {
            List<Customer> sameEmailCustomers = customerRepository.findByEmail(email);
            boolean duplicated = sameEmailCustomers.stream()
                    .anyMatch(c -> !c.getId().equals(currentCustomerId));

            if (duplicated) {
                throw new RuntimeException("Email đã tồn tại");
            }
        }

        String loaiKhach = request.getLoaiKhach();
        if (loaiKhach != null && !loaiKhach.isBlank()) {
            String normalized = loaiKhach.trim().toUpperCase();
            if (!normalized.equals("VIP") && !normalized.equals("THUONG")
                    && !normalized.equals("SILVER") && !normalized.equals("GOLD")) {
                throw new RuntimeException("Loại khách không hợp lệ");
            }
        }

        if (request.getDiemTichLuy() != null && request.getDiemTichLuy() < 0) {
            throw new RuntimeException("Điểm tích lũy không được âm");
        }
    }

    private boolean matchKeyword(CustomerResponse c, String keyword) {
        String normalized = safeTrim(keyword).toLowerCase();
        if (normalized.isBlank()) {
            return true;
        }

        return safe(c.getTen()).toLowerCase().contains(normalized)
                || safe(c.getEmail()).toLowerCase().contains(normalized)
                || safe(c.getSdt()).contains(normalized);
    }

    private boolean matchLoaiKhach(CustomerResponse c, String loaiKhach) {
        String normalized = safeTrim(loaiKhach).toUpperCase();
        if (normalized.isBlank()) {
            return true;
        }
        return safe(c.getLoaiKhach()).equalsIgnoreCase(normalized);
    }

    private boolean matchPoints(CustomerResponse c, Integer minPoints, Integer maxPoints) {
        int points = c.getDiemTichLuy() == null ? 0 : c.getDiemTichLuy();

        if (minPoints != null && points < minPoints) {
            return false;
        }
        if (maxPoints != null && points > maxPoints) {
            return false;
        }

        return true;
    }

    private boolean matchSpending(CustomerResponse c, BigDecimal minSpending, BigDecimal maxSpending) {
        BigDecimal spending = c.getTongChiTieu() == null ? BigDecimal.ZERO : c.getTongChiTieu();

        if (minSpending != null && spending.compareTo(minSpending) < 0) {
            return false;
        }
        if (maxSpending != null && spending.compareTo(maxSpending) > 0) {
            return false;
        }

        return true;
    }

    private boolean matchInactiveDays(CustomerResponse c, Integer inactiveDays) {
        if (inactiveDays == null || inactiveDays <= 0) {
            return true;
        }
        long days = c.getSoNgayKhongMua() == null ? 0L : c.getSoNgayKhongMua();
        return days >= inactiveDays;
    }

    private Comparator<CustomerResponse> buildComparator(String sortBy) {
        String normalized = safeTrim(sortBy).toLowerCase();

        switch (normalized) {
            case "name":
                return Comparator.comparing(new java.util.function.Function<CustomerResponse, String>() {
                    @Override
                    public String apply(CustomerResponse c) {
                        return safe(c.getTen());
                    }
                }, String.CASE_INSENSITIVE_ORDER);

            case "points-desc":
                return Comparator.comparing(new java.util.function.Function<CustomerResponse, Integer>() {
                    @Override
                    public Integer apply(CustomerResponse c) {
                        return c.getDiemTichLuy() == null ? 0 : c.getDiemTichLuy();
                    }
                }).reversed();

            case "spending-desc":
                return Comparator.comparing(new java.util.function.Function<CustomerResponse, BigDecimal>() {
                    @Override
                    public BigDecimal apply(CustomerResponse c) {
                        return c.getTongChiTieu() == null ? BigDecimal.ZERO : c.getTongChiTieu();
                    }
                }).reversed();

            case "inactive-desc":
                return Comparator.comparing(new java.util.function.Function<CustomerResponse, Long>() {
                    @Override
                    public Long apply(CustomerResponse c) {
                        return c.getSoNgayKhongMua() == null ? 0L : c.getSoNgayKhongMua();
                    }
                }).reversed();

            default:
                return Comparator.comparing(new java.util.function.Function<CustomerResponse, Integer>() {
                    @Override
                    public Integer apply(CustomerResponse c) {
                        return c.getId() == null ? 0 : c.getId();
                    }
                }).reversed();
        }
    }

    private Long calculateDaysSince(LocalDateTime lastOrderDate, LocalDateTime createdAt) {
        LocalDateTime baseDate = lastOrderDate != null ? lastOrderDate : createdAt;
        if (baseDate == null) {
            return 0L;
        }
        return Duration.between(baseDate, LocalDateTime.now()).toDays();
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String toNullIfBlank(String value) {
        String trimmed = safeTrim(value);
        return trimmed.isBlank() ? null : trimmed;
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        if (value instanceof Short) {
            return ((Short) value).intValue();
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof Short) {
            return ((Short) value).longValue();
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    private String toStringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Integer) {
            return BigDecimal.valueOf((Integer) value);
        }
        if (value instanceof Long) {
            return BigDecimal.valueOf((Long) value);
        }
        if (value instanceof Double) {
            return BigDecimal.valueOf((Double) value);
        }
        if (value instanceof Float) {
            return BigDecimal.valueOf(((Float) value).doubleValue());
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return new BigDecimal(value.toString());
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        }
        return LocalDateTime.parse(value.toString().replace(" ", "T"));
    }

    private LocalDate toLocalDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        return LocalDate.parse(value.toString());
    }
}