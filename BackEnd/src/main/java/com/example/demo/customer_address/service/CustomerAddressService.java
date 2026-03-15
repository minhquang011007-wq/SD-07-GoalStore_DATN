package com.example.demo.customer_address.service;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.customer_address.dto.CustomerAddressRequest;
import com.example.demo.customer_address.dto.CustomerAddressResponse;
import com.example.demo.customer_address.entity.CustomerAddress;
import com.example.demo.customer_address.repository.CustomerAddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerAddressService {
    private final CustomerAddressRepository repository;
    private final CustomerRepository customerRepository;

    public CustomerAddressService(CustomerAddressRepository repository, CustomerRepository customerRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    public List<CustomerAddressResponse> getByCustomer(Integer customerId) {
        return repository.findByCustomerIdOrderByIdDesc(customerId).stream().map(this::toResponse).toList();
    }

    public CustomerAddressResponse create(CustomerAddressRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        CustomerAddress address = new CustomerAddress();
        map(request, address, customer);
        return toResponse(repository.save(address));
    }

    public CustomerAddressResponse update(Integer id, CustomerAddressRequest request) {
        CustomerAddress address = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        map(request, address, customer);
        return toResponse(repository.save(address));
    }

    public void delete(Integer id) { repository.deleteById(id); }

    private void map(CustomerAddressRequest request, CustomerAddress address, Customer customer) {
        address.setCustomer(customer);
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setWard(request.getWard());
        address.setDetailAddress(request.getDetailAddress());
        address.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()));
    }

    private CustomerAddressResponse toResponse(CustomerAddress address) {
        CustomerAddressResponse response = new CustomerAddressResponse();
        response.setId(address.getId());
        response.setCustomerId(address.getCustomer().getId());
        response.setReceiverName(address.getReceiverName());
        response.setReceiverPhone(address.getReceiverPhone());
        response.setProvince(address.getProvince());
        response.setDistrict(address.getDistrict());
        response.setWard(address.getWard());
        response.setDetailAddress(address.getDetailAddress());
        response.setIsDefault(address.getIsDefault());
        return response;
    }
}
