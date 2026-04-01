package com.example.demo.loyalty.controller;

import com.example.demo.loyalty.dto.*;
import com.example.demo.loyalty.service.LoyaltyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loyalty")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addPoint(@RequestBody AddPointRequest request) {
        loyaltyService.addPoint(request);
        return ResponseEntity.ok("Cộng điểm thành công");
    }

    @PostMapping("/deduct")
    public ResponseEntity<String> deductPoint(@RequestBody DeductPointRequest request) {
        loyaltyService.deductPoint(request);
        return ResponseEntity.ok("Trừ điểm thành công");
    }

    @PostMapping("/vip/assign")
    public ResponseEntity<String> assignVip(@RequestBody AssignVipRequest request) {
        loyaltyService.assignVip(request);
        return ResponseEntity.ok("Gán VIP thành công");
    }

    @PostMapping("/vip-program")
    public ResponseEntity<String> createVipProgram(@RequestBody VipProgramRequest request) {
        loyaltyService.createVipProgram(request);
        return ResponseEntity.ok("Tạo chương trình VIP thành công");
    }

    @PutMapping("/vip-program/{id}")
    public ResponseEntity<VipProgramResponse> updateVipProgram(
            @PathVariable Integer id,
            @RequestBody VipProgramRequest request
    ) {
        return ResponseEntity.ok(loyaltyService.updateVipProgram(id, request));
    }

    @PostMapping("/reward-rule")
    public ResponseEntity<String> createRewardRule(@RequestBody RewardRuleRequest request) {
        loyaltyService.createRewardRule(request);
        return ResponseEntity.ok("Tạo ưu đãi thành công");
    }

    @PutMapping("/reward-rule/{id}")
    public ResponseEntity<RewardRuleResponse> updateRewardRule(
            @PathVariable Integer id,
            @RequestBody RewardRuleRequest request
    ) {
        return ResponseEntity.ok(loyaltyService.updateRewardRule(id, request));
    }

    @PostMapping("/redeem")
    public ResponseEntity<RedeemRewardResponse> redeemReward(@RequestBody RedeemRewardRequest request) {
        return ResponseEntity.ok(loyaltyService.redeemReward(request));
    }

    @GetMapping("/history/{customerId}")
    public ResponseEntity<List<LoyaltyHistoryResponse>> getHistory(@PathVariable Integer customerId) {
        return ResponseEntity.ok(loyaltyService.getHistory(customerId));
    }

    @PostMapping("/history/filter")
    public ResponseEntity<List<LoyaltyHistoryResponse>> filterHistory(
            @RequestBody LoyaltyHistoryFilterRequest request
    ) {
        return ResponseEntity.ok(loyaltyService.filterHistory(request));
    }

    @GetMapping("/customers")
    public ResponseEntity<List<LoyaltyCustomerResponse>> getAllLoyaltyCustomers() {
        return ResponseEntity.ok(loyaltyService.getAllLoyaltyCustomers());
    }

    @GetMapping("/vip-programs")
    public ResponseEntity<List<VipProgramResponse>> getVipPrograms() {
        return ResponseEntity.ok(loyaltyService.getVipPrograms());
    }

    @GetMapping("/vip-history/{customerId}")
    public ResponseEntity<List<VipHistoryResponse>> getVipHistory(@PathVariable Integer customerId) {
        return ResponseEntity.ok(loyaltyService.getVipHistory(customerId));
    }

    @GetMapping("/reward-rules")
    public ResponseEntity<List<RewardRuleResponse>> getRewardRules() {
        return ResponseEntity.ok(loyaltyService.getRewardRules());
    }

    @GetMapping("/birthday-logs/{customerId}")
    public ResponseEntity<List<BirthdayNotificationResponse>> getBirthdayLogs(@PathVariable Integer customerId) {
        return ResponseEntity.ok(loyaltyService.getBirthdayLogs(customerId));
    }

    @PostMapping("/birthday-log")
    public ResponseEntity<BirthdayNotificationResponse> createBirthdayLog(
            @RequestBody BirthdayNotificationRequest request
    ) {
        return ResponseEntity.ok(loyaltyService.createBirthdayNotification(request));
    }

    @GetMapping("/birthdays/today")
    public ResponseEntity<List<BirthdayNotificationResponse>> getTodayBirthdays() {
        return ResponseEntity.ok(loyaltyService.getTodayBirthdayCustomers());
    }

    @GetMapping("/birthdays/month")
    public ResponseEntity<List<BirthdayNotificationResponse>> getCurrentMonthBirthdays() {
        return ResponseEntity.ok(loyaltyService.getCurrentMonthBirthdayCustomers());
    }
}