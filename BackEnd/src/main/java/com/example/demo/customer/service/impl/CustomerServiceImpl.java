package com.example.demo.customer.service.impl;

import com.example.demo.customer.dto.CustomerRequest;
import com.example.demo.customer.dto.CustomerResponse;
import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.customer.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với id = " + id));
        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        mapToEntity(request, customer);

        Customer savedCustomer = customerRepository.save(customer);
        return mapToResponse(savedCustomer);
    }

    @Override
    public CustomerResponse updateCustomer(Integer id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với id = " + id));

        mapToEntity(request, customer);

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với id = " + id));

        customerRepository.delete(customer);
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

    private void mapToEntity(CustomerRequest request, Customer customer) {
        customer.setTen(request.getTen());
        customer.setEmail(request.getEmail());
        customer.setSdt(request.getSdt());
        customer.setNgaySinh(request.getNgaySinh());
        customer.setGhiChu(request.getGhiChu());

        if (request.getLoaiKhach() != null && !request.getLoaiKhach().isBlank()) {
            customer.setLoaiKhach(request.getLoaiKhach());
        } else if (customer.getLoaiKhach() == null) {
            customer.setLoaiKhach("THUONG");
        }

        if (request.getDiemTichLuy() != null) {
            customer.setDiemTichLuy(request.getDiemTichLuy());
        } else if (customer.getDiemTichLuy() == null) {
            customer.setDiemTichLuy(0);
        }
    }
}