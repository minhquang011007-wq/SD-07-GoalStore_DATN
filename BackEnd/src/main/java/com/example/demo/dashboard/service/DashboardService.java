package com.example.demo.dashboard.service;

import com.example.demo.dashboard.dto.AdminDashboardResponse;
import com.example.demo.dashboard.dto.CustomerDashboardResponse;

public interface DashboardService {
    AdminDashboardResponse getAdminDashboard(Integer year);
    CustomerDashboardResponse getCustomerDashboard(Integer customerId, Integer year);
}
