package com.example.demo.customer.service.impl;

import com.example.demo.customer.dto.*;
import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.customer.service.CustomerService;
import com.example.demo.order.entity.Order;
import com.example.demo.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        mapToEntity(request, customer);
        return mapToResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse updateCustomer(Integer id, CustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        mapToEntity(request, customer);
        return mapToResponse(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Integer id) {
        customerRepository.deleteById(id);
    }

    @Override
    public List<CustomerResponse> searchCustomerByName(String name) {
        return customerRepository.findByTenContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponse> getCustomersByLoaiKhach(String loaiKhach) {
        return customerRepository.findByLoaiKhach(loaiKhach.toUpperCase())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponse> searchByEmail(String email) {
        return customerRepository.findByEmail(email)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponse> searchByPhone(String sdt) {
        return customerRepository.findBySdt(sdt)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponse> searchAll(String keyword) {

        return customerRepository.searchAll(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

    }

    @Override
    public List<CustomerOrderHistoryResponse> getCustomerOrderHistory(Integer customerId) {

        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId)
                .stream()
                .map(this::mapOrderHistory)
                .collect(Collectors.toList());

    }

    @Override
    public List<CustomerSpendingResponse> getTopCustomersBySpending() {

        List<Object[]> rows = orderRepository.getCustomerSpending();

        return rows.stream().map(row -> {

            Integer customerId = (Integer) row[0];
            BigDecimal spending = (BigDecimal) row[1];

            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow();

            CustomerSpendingResponse response = new CustomerSpendingResponse();

            response.setCustomerId(customer.getId());
            response.setTen(customer.getTen());
            response.setEmail(customer.getEmail());
            response.setSdt(customer.getSdt());
            response.setLoaiKhach(customer.getLoaiKhach());
            response.setDiemTichLuy(customer.getDiemTichLuy());
            response.setTongChiTieu(spending);

            return response;

        }).collect(Collectors.toList());

    }

    @Override
    public List<InactiveCustomerResponse> getInactiveCustomers(Long days) {

        LocalDateTime now = LocalDateTime.now();

        return orderRepository.getLastOrderDateByCustomer()
                .stream()
                .map(row -> {

                    Integer customerId = (Integer) row[0];
                    String ten = (String) row[1];
                    String email = (String) row[2];
                    String sdt = (String) row[3];
                    LocalDateTime lastOrderDate = (LocalDateTime) row[4];

                    long diffDays = Duration.between(lastOrderDate, now).toDays();

                    InactiveCustomerResponse response = new InactiveCustomerResponse();

                    response.setCustomerId(customerId);
                    response.setTen(ten);
                    response.setEmail(email);
                    response.setSdt(sdt);
                    response.setLastOrderDate(lastOrderDate);
                    response.setSoNgayKhongMua(diffDays);

                    return response;

                })
                .filter(item -> item.getSoNgayKhongMua() >= days)
                .collect(Collectors.toList());

    }

    @Override
    public CustomerDetailResponse getCustomerDetail(Integer id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow();

        CustomerDetailResponse res = new CustomerDetailResponse();

        res.setId(customer.getId());
        res.setTen(customer.getTen());
        res.setEmail(customer.getEmail());
        res.setSdt(customer.getSdt());
        res.setLoaiKhach(customer.getLoaiKhach());
        res.setDiemTichLuy(customer.getDiemTichLuy());

        Integer totalOrders = orderRepository.countOrders(id);
        BigDecimal totalSpending = orderRepository.sumSpending(id);

        res.setTotalOrders(totalOrders);
        res.setTotalSpending(totalSpending);

        return res;

    }

    private CustomerResponse mapToResponse(Customer customer) {

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

    private CustomerOrderHistoryResponse mapOrderHistory(Order order) {

        CustomerOrderHistoryResponse response = new CustomerOrderHistoryResponse();

        response.setOrderId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setChannel(order.getChannel());
        response.setTotal(order.getTotal());

        return response;

    }

    private void mapToEntity(CustomerRequest request, Customer customer) {

        customer.setTen(request.getTen());
        customer.setEmail(request.getEmail());
        customer.setSdt(request.getSdt());
        customer.setNgaySinh(request.getNgaySinh());
        customer.setGhiChu(request.getGhiChu());

        if (request.getLoaiKhach() != null) {
            customer.setLoaiKhach(request.getLoaiKhach().toUpperCase());
        }

        if (request.getDiemTichLuy() != null) {
            customer.setDiemTichLuy(request.getDiemTichLuy());
        }

    }

}