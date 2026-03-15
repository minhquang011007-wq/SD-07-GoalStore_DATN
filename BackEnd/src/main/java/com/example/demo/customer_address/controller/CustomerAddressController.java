package com.example.demo.customer_address.controller;

import com.example.demo.customer_address.dto.CustomerAddressRequest;
import com.example.demo.customer_address.dto.CustomerAddressResponse;
import com.example.demo.customer_address.service.CustomerAddressService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-addresses")
public class CustomerAddressController {
    private final CustomerAddressService service;

    public CustomerAddressController(CustomerAddressService service) {
        this.service = service;
    }

    @GetMapping
    public List<CustomerAddressResponse> getByCustomer(@RequestParam Integer customerId) {
        return service.getByCustomer(customerId);
    }

    @PostMapping
    public CustomerAddressResponse create(@RequestBody CustomerAddressRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public CustomerAddressResponse update(@PathVariable Integer id, @RequestBody CustomerAddressRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "Xóa địa chỉ thành công";
    }
}
