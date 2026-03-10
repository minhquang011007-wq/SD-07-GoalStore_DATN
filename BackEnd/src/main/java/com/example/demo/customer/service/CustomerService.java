package com.example.demo.customer.service;

import com.example.demo.customer.dto.*;

import java.util.List;

public interface CustomerService {

    List<CustomerResponse> getAllCustomers();

    CustomerResponse getCustomerById(Integer id);

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse updateCustomer(Integer id, CustomerRequest request);

    void deleteCustomer(Integer id);

    List<CustomerResponse> searchCustomerByName(String name);

    List<CustomerResponse> getCustomersByLoaiKhach(String loaiKhach);

    List<CustomerResponse> searchByEmail(String email);

    List<CustomerResponse> searchByPhone(String sdt);

    List<CustomerOrderHistoryResponse> getCustomerOrderHistory(Integer customerId);

    List<CustomerSpendingResponse> getTopCustomersBySpending();

    List<InactiveCustomerResponse> getInactiveCustomers(Long days);
}