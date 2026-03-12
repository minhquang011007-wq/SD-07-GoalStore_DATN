package com.example.demo.customer.controller;

import com.example.demo.customer.dto.*;
import com.example.demo.customer.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<CustomerDetailResponse> getCustomerDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getCustomerDetail(id));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Integer id,
            @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Xóa khách hàng thành công");
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> searchCustomer(@RequestParam String name) {
        return ResponseEntity.ok(customerService.searchCustomerByName(name));
    }

    @GetMapping("/search-all")
    public ResponseEntity<List<CustomerResponse>> searchAll(@RequestParam String keyword) {
        return ResponseEntity.ok(customerService.searchAll(keyword));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CustomerResponse>> filterByLoaiKhach(@RequestParam String loaiKhach) {
        return ResponseEntity.ok(customerService.getCustomersByLoaiKhach(loaiKhach));
    }

    @GetMapping("/email")
    public ResponseEntity<List<CustomerResponse>> searchByEmail(@RequestParam String email) {
        return ResponseEntity.ok(customerService.searchByEmail(email));
    }

    @GetMapping("/phone")
    public ResponseEntity<List<CustomerResponse>> searchByPhone(@RequestParam String sdt) {
        return ResponseEntity.ok(customerService.searchByPhone(sdt));
    }

    @GetMapping("/{customerId}/orders")
    public ResponseEntity<List<CustomerOrderHistoryResponse>> getCustomerOrders(@PathVariable Integer customerId) {
        return ResponseEntity.ok(customerService.getCustomerOrderHistory(customerId));
    }

    @GetMapping("/top-spending")
    public ResponseEntity<List<CustomerSpendingResponse>> getTopSpendingCustomers() {
        return ResponseEntity.ok(customerService.getTopCustomersBySpending());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<InactiveCustomerResponse>> getInactiveCustomers(@RequestParam Long days) {
        return ResponseEntity.ok(customerService.getInactiveCustomers(days));
    }

}