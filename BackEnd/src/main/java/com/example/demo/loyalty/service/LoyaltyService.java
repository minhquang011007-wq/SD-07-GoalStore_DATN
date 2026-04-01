package com.example.demo.loyalty.service;

import com.example.demo.loyalty.dto.*;

import java.util.List;

public interface LoyaltyService {

    void addPoint(AddPointRequest request);

    void deductPoint(DeductPointRequest request);

    List<LoyaltyHistoryResponse> getHistory(Integer customerId);

    List<LoyaltyHistoryResponse> filterHistory(LoyaltyHistoryFilterRequest request);

    List<LoyaltyCustomerResponse> getAllLoyaltyCustomers();

    List<VipProgramResponse> getVipPrograms();

    List<VipHistoryResponse> getVipHistory(Integer customerId);

    List<RewardRuleResponse> getRewardRules();

    List<BirthdayNotificationResponse> getBirthdayLogs(Integer customerId);

    void assignVip(AssignVipRequest request);

    void createVipProgram(VipProgramRequest request);

    VipProgramResponse updateVipProgram(Integer id, VipProgramRequest request);

    void createRewardRule(RewardRuleRequest request);

    RewardRuleResponse updateRewardRule(Integer id, RewardRuleRequest request);

    RedeemRewardResponse redeemReward(RedeemRewardRequest request);

    BirthdayNotificationResponse createBirthdayNotification(BirthdayNotificationRequest request);

    List<BirthdayNotificationResponse> getTodayBirthdayCustomers();

    List<BirthdayNotificationResponse> getCurrentMonthBirthdayCustomers();
}