package com.example.demo.dashboard.controller;

import com.example.demo.dashboard.dto.AdminDashboardResponse;
import com.example.demo.dashboard.dto.CustomerDashboardResponse;
import com.example.demo.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard(
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(dashboardService.getAdminDashboard(year));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CustomerDashboardResponse> getCustomerDashboard(
            @PathVariable Integer customerId,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(dashboardService.getCustomerDashboard(customerId, year));
    }
}
