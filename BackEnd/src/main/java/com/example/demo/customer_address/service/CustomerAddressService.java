package com.example.demo.customer_address.service;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.customer_address.dto.CustomerAddressRequest;
import com.example.demo.customer_address.dto.CustomerAddressResponse;
import com.example.demo.customer_address.entity.CustomerAddress;
import com.example.demo.customer_address.repository.CustomerAddressRepository;
import jakarta.transaction.Transactional;
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

    public CustomerAddressResponse getDefaultAddress(Integer customerId) {
        CustomerAddress address = getDefaultAddressEntity(customerId);
        return toResponse(address);
    }

    @Transactional
    public CustomerAddressResponse create(CustomerAddressRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        CustomerAddress address = new CustomerAddress();
        map(request, address, customer);
        CustomerAddress saved = repository.save(address);
        ensureSingleDefault(customer.getId(), saved.getId(), saved.getIsDefault());
        return toResponse(saved);
    }

    @Transactional
    public CustomerAddressResponse update(Integer id, CustomerAddressRequest request) {
        CustomerAddress address = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        map(request, address, customer);
        CustomerAddress saved = repository.save(address);
        ensureSingleDefault(customer.getId(), saved.getId(), saved.getIsDefault());
        return toResponse(saved);
    }

    public void delete(Integer id) { repository.deleteById(id); }

    public CustomerAddress getDefaultAddressEntity(Integer customerId) {
        return repository.findFirstByCustomerIdAndIsDefaultTrueOrderByIdDesc(customerId)
                .orElseGet(() -> repository.findByCustomerIdOrderByIdDesc(customerId).stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Khách hàng chưa có địa chỉ nhận hàng")));
    }

    public CustomerAddress getAddressOfCustomer(Integer customerId, Integer addressId) {
        return repository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không thuộc khách hàng này"));
    }

    public String buildFullAddress(CustomerAddress address) {
        StringBuilder sb = new StringBuilder();
        appendPart(sb, address.getDetailAddress());
        appendPart(sb, address.getWard());
        appendPart(sb, address.getDistrict());
        appendPart(sb, address.getProvince());
        return sb.toString();
    }

    private void appendPart(StringBuilder sb, String value) {
        if (value == null || value.isBlank()) return;
        if (!sb.isEmpty()) sb.append(", ");
        sb.append(value.trim());
    }

    private void ensureSingleDefault(Integer customerId, Integer addressId, Boolean isDefault) {
        boolean shouldBeDefault = Boolean.TRUE.equals(isDefault)
                || repository.findByCustomerIdOrderByIdDesc(customerId).size() == 1;
        if (shouldBeDefault) {
            repository.resetDefaultByCustomerId(customerId, addressId);
            CustomerAddress address = repository.findById(addressId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
            if (!Boolean.TRUE.equals(address.getIsDefault())) {
                address.setIsDefault(true);
                repository.save(address);
            }
        }
    }

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
