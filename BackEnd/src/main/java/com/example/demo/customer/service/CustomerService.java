package com.example.demo.customer.service;

import com.example.demo.customer.dto.*;

import java.util.List;

public interface CustomerService {

    List<CustomerResponse> getAllCustomers();

    CustomerStatsResponse getCustomerStats();

    List<CustomerActivityResponse> getCustomerActivity(Integer days);

    List<CustomerResponse> filterCustomers(CustomerFilterRequest request);

    CustomerResponse getCustomerById(Integer id);

    CustomerDetailResponse getCustomerDetail(Integer id);

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse updateCustomer(Integer id, CustomerRequest request);

    CustomerResponse assignVip(Integer id);

    void deleteCustomer(Integer id);

    List<CustomerResponse> searchCustomerByName(String name);

    List<CustomerResponse> getCustomersByLoaiKhach(String loaiKhach);

    List<CustomerResponse> searchByEmail(String email);

    List<CustomerResponse> searchByPhone(String sdt);

    List<CustomerResponse> searchAll(String keyword);

    List<CustomerOrderHistoryResponse> getCustomerOrderHistory(Integer customerId);

    List<CustomerSpendingResponse> getTopCustomersBySpending();

    List<TopLoyalCustomerResponse> getTopCustomersByPoints(Integer limit);

    List<InactiveCustomerResponse> getInactiveCustomers(Long days);
}