package com.example.demo.dashboard.service.impl;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.dashboard.dto.*;
import com.example.demo.dashboard.service.DashboardService;
import com.example.demo.order.entity.Order;
import com.example.demo.order.repository.OrderRepository;
import com.example.demo.voucher.entity.CustomerVoucher;
import com.example.demo.voucher.entity.Voucher;
import com.example.demo.voucher.repository.CustomerVoucherRepository;
import com.example.demo.voucher.repository.VoucherRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final VoucherRepository voucherRepository;
    private final CustomerVoucherRepository customerVoucherRepository;

    public DashboardServiceImpl(OrderRepository orderRepository,
                                CustomerRepository customerRepository,
                                VoucherRepository voucherRepository,
                                CustomerVoucherRepository customerVoucherRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.voucherRepository = voucherRepository;
        this.customerVoucherRepository = customerVoucherRepository;
    }

    @Override
    public AdminDashboardResponse getAdminDashboard(Integer year) {
        int targetYear = normalizeYear(year);
        List<Order> allOrders = orderRepository.findAllWithCustomerOrderByOrderDateDesc();
        List<Order> ordersInYear = allOrders.stream()
                .filter(order -> isInYear(order.getOrderDate(), targetYear))
                .toList();
        List<Customer> customers = customerRepository.findAll();
        List<Voucher> vouchers = voucherRepository.findByIsDeletedFalseOrderByIdDesc();
        List<CustomerVoucher> customerVouchers = customerVoucherRepository.findAll();

        AdminDashboardResponse response = new AdminDashboardResponse();
        response.setYear(targetYear);

        AdminDashboardResponse.Summary summary = new AdminDashboardResponse.Summary();
        long totalOrders = ordersInYear.size();
        long completedOrders = countByStatus(ordersInYear, "HOAN_TAT");
        long cancelledOrders = countByStatus(ordersInYear, "HUY");
        long returnOrders = countByStatus(ordersInYear, "TRA_HANG");
        BigDecimal totalRevenue = sumRevenue(ordersInYear, true);
        BigDecimal averageOrderValue = completedOrders == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(completedOrders), 0, RoundingMode.HALF_UP);
        summary.setTotalOrders(totalOrders);
        summary.setCompletedOrders(completedOrders);
        summary.setCancelledOrders(cancelledOrders);
        summary.setReturnOrders(returnOrders);
        summary.setTotalRevenue(totalRevenue);
        summary.setAverageOrderValue(averageOrderValue);
        response.setSummary(summary);

        AdminDashboardResponse.CustomerSection customerSection = new AdminDashboardResponse.CustomerSection();
        long totalCustomers = customers.size();
        long vipCustomers = customers.stream().filter(item -> "VIP".equalsIgnoreCase(item.getLoaiKhach())).count();
        long activeCustomers30Days = customers.stream()
                .filter(customer -> {
                    LocalDateTime lastOrderDate = orderRepository.findLastOrderDateByCustomerId(customer.getId());
                    return lastOrderDate != null && !lastOrderDate.isBefore(LocalDateTime.now().minusDays(30));
                })
                .count();
        long newCustomersThisYear = customers.stream()
                .filter(customer -> customer.getCreatedAt() != null && customer.getCreatedAt().getYear() == targetYear)
                .count();
        long totalPoints = customers.stream().map(Customer::getDiemTichLuy).filter(Objects::nonNull).mapToLong(Integer::longValue).sum();
        customerSection.setTotalCustomers(totalCustomers);
        customerSection.setVipCustomers(vipCustomers);
        customerSection.setRegularCustomers(Math.max(0, totalCustomers - vipCustomers));
        customerSection.setActiveCustomers30Days(activeCustomers30Days);
        customerSection.setNewCustomersThisYear(newCustomersThisYear);
        customerSection.setTotalPoints(totalPoints);
        response.setCustomers(customerSection);

        AdminDashboardResponse.VoucherSection voucherSection = new AdminDashboardResponse.VoucherSection();
        long claimedVouchers = customerVouchers.size();
        long usedVouchers = customerVouchers.stream().filter(item -> item.getUsedAt() != null).count();
        long totalVoucherQuantity = vouchers.stream().map(Voucher::getTotalQuantity).filter(Objects::nonNull).mapToLong(Integer::longValue).sum();
        voucherSection.setTotalVouchers(vouchers.size());
        voucherSection.setActiveVouchers(vouchers.stream().filter(item -> Boolean.TRUE.equals(item.getIsActive())).count());
        voucherSection.setClaimedVouchers(claimedVouchers);
        voucherSection.setUsedVouchers(usedVouchers);
        voucherSection.setRemainingVouchers(Math.max(0, totalVoucherQuantity - claimedVouchers));
        voucherSection.setTotalVoucherQuantity(totalVoucherQuantity);
        response.setVouchers(voucherSection);

        response.setRevenueByMonth(buildMonthlyRevenue(ordersInYear));
        response.setOrdersByMonth(buildMonthlyOrders(ordersInYear));
        response.setOrderStatusBreakdown(buildStatusMetrics(ordersInYear));
        response.setTopCustomers(buildTopCustomers(ordersInYear));

        return response;
    }

    @Override
    public CustomerDashboardResponse getCustomerDashboard(Integer customerId, Integer year) {
        int targetYear = normalizeYear(year);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        List<Order> customerOrders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
        List<Order> ordersInYear = customerOrders.stream()
                .filter(order -> isInYear(order.getOrderDate(), targetYear))
                .toList();
        List<CustomerVoucher> wallet = customerVoucherRepository.findByCustomerIdWithVoucher(customerId);

        CustomerDashboardResponse response = new CustomerDashboardResponse();
        response.setCustomerId(customer.getId());
        response.setCustomerName(customer.getTen());
        response.setLoaiKhach(customer.getLoaiKhach());
        response.setLoyaltyPoints(customer.getDiemTichLuy() == null ? 0 : customer.getDiemTichLuy());

        CustomerDashboardResponse.Summary summary = new CustomerDashboardResponse.Summary();
        long totalOrders = ordersInYear.size();
        long completedOrders = countByStatus(ordersInYear, "HOAN_TAT");
        long cancelledOrders = countByStatus(ordersInYear, "HUY");
        long returnOrders = countByStatus(ordersInYear, "TRA_HANG");
        BigDecimal totalSpent = sumRevenue(ordersInYear, false);
        BigDecimal averageOrderValue = totalOrders == 0
                ? BigDecimal.ZERO
                : totalSpent.divide(BigDecimal.valueOf(totalOrders), 0, RoundingMode.HALF_UP);
        summary.setTotalOrders(totalOrders);
        summary.setCompletedOrders(completedOrders);
        summary.setCancelledOrders(cancelledOrders);
        summary.setReturnOrders(returnOrders);
        summary.setTotalSpent(totalSpent);
        summary.setAverageOrderValue(averageOrderValue);
        response.setSummary(summary);

        CustomerDashboardResponse.VoucherWallet voucherWallet = new CustomerDashboardResponse.VoucherWallet();
        long used = wallet.stream().filter(item -> item.getUsedAt() != null).count();
        voucherWallet.setClaimed(wallet.size());
        voucherWallet.setUsed(used);
        voucherWallet.setAvailable(Math.max(0, wallet.size() - used));
        response.setWallet(voucherWallet);

        response.setSpendingByMonth(buildMonthlySpending(ordersInYear));
        response.setOrderStatusBreakdown(buildStatusMetrics(ordersInYear));
        return response;
    }

    private List<MonthlyMetric> buildMonthlyRevenue(List<Order> orders) {
        List<MonthlyMetric> metrics = initMonthlyMetrics();
        for (Order order : orders) {
            if (order.getOrderDate() == null || !"HOAN_TAT".equalsIgnoreCase(order.getStatus())) {
                continue;
            }
            int monthIndex = order.getOrderDate().getMonthValue() - 1;
            BigDecimal current = metrics.get(monthIndex).getValue();
            metrics.get(monthIndex).setValue(current.add(safeAmount(order.getTotal())));
        }
        return metrics;
    }

    private List<MonthlyMetric> buildMonthlyOrders(List<Order> orders) {
        List<MonthlyMetric> metrics = initMonthlyMetrics();
        for (Order order : orders) {
            if (order.getOrderDate() == null) {
                continue;
            }
            int monthIndex = order.getOrderDate().getMonthValue() - 1;
            BigDecimal current = metrics.get(monthIndex).getValue();
            metrics.get(monthIndex).setValue(current.add(BigDecimal.ONE));
        }
        return metrics;
    }

    private List<MonthlyMetric> buildMonthlySpending(List<Order> orders) {
        List<MonthlyMetric> metrics = initMonthlyMetrics();
        for (Order order : orders) {
            if (order.getOrderDate() == null) {
                continue;
            }
            int monthIndex = order.getOrderDate().getMonthValue() - 1;
            BigDecimal current = metrics.get(monthIndex).getValue();
            metrics.get(monthIndex).setValue(current.add(safeAmount(order.getTotal())));
        }
        return metrics;
    }

    private List<MonthlyMetric> initMonthlyMetrics() {
        List<MonthlyMetric> metrics = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            String label = LocalDate.of(2000, month, 1).getMonth().getDisplayName(TextStyle.SHORT, new Locale("vi", "VN"));
            metrics.add(new MonthlyMetric(month, label, BigDecimal.ZERO));
        }
        return metrics;
    }

    private List<StatusMetric> buildStatusMetrics(List<Order> orders) {
        Map<String, String> labels = new LinkedHashMap<>();
        labels.put("MOI", "Mới");
        labels.put("DANG_XU_LY", "Đang xử lý");
        labels.put("DANG_GIAO", "Đang giao");
        labels.put("HOAN_TAT", "Hoàn tất");
        labels.put("HUY", "Hủy");
        labels.put("TRA_HANG", "Trả hàng");

        List<StatusMetric> metrics = new ArrayList<>();
        for (Map.Entry<String, String> entry : labels.entrySet()) {
            long count = countByStatus(orders, entry.getKey());
            metrics.add(new StatusMetric(entry.getKey(), entry.getValue(), count));
        }
        return metrics;
    }

    private List<TopCustomerMetric> buildTopCustomers(List<Order> orders) {
        Map<Integer, List<Order>> grouped = orders.stream()
                .filter(order -> order.getCustomer() != null && order.getCustomer().getId() != null)
                .collect(Collectors.groupingBy(order -> order.getCustomer().getId()));

        return grouped.entrySet().stream()
                .map(entry -> {
                    List<Order> customerOrders = entry.getValue();
                    Customer customer = customerOrders.get(0).getCustomer();
                    TopCustomerMetric metric = new TopCustomerMetric();
                    metric.setCustomerId(customer.getId());
                    metric.setCustomerName(customer.getTen());
                    metric.setLoaiKhach(customer.getLoaiKhach());
                    metric.setOrderCount((long) customerOrders.size());
                    metric.setTotalSpent(sumRevenue(customerOrders, false));
                    metric.setLoyaltyPoints(customer.getDiemTichLuy() == null ? 0 : customer.getDiemTichLuy());
                    return metric;
                })
                .sorted(Comparator.comparing(TopCustomerMetric::getTotalSpent).reversed())
                .limit(5)
                .toList();
    }

    private long countByStatus(List<Order> orders, String status) {
        return orders.stream().filter(order -> status.equalsIgnoreCase(order.getStatus())).count();
    }

    private BigDecimal sumRevenue(List<Order> orders, boolean onlyCompleted) {
        return orders.stream()
                .filter(order -> !onlyCompleted || "HOAN_TAT".equalsIgnoreCase(order.getStatus()))
                .map(Order::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private boolean isInYear(LocalDateTime time, int year) {
        return time != null && time.getYear() == year;
    }

    private int normalizeYear(Integer year) {
        int currentYear = Year.now().getValue();
        if (year == null || year < 2000 || year > currentYear + 1) {
            return currentYear;
        }
        return year;
    }
}
