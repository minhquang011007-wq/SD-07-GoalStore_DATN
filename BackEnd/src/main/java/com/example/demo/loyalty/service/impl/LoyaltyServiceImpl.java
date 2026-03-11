package com.example.demo.loyalty.service.impl;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.loyalty.dto.*;
import com.example.demo.loyalty.entity.*;
import com.example.demo.loyalty.repository.*;
import com.example.demo.loyalty.service.LoyaltyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyRepository loyaltyRepository;
    private final CustomerRepository customerRepository;
    private final VipProgramRepository vipProgramRepository;
    private final VipHistoryRepository vipHistoryRepository;
    private final RewardRuleRepository rewardRuleRepository;
    private final BirthdayNotificationLogRepository birthdayNotificationLogRepository;

    public LoyaltyServiceImpl(LoyaltyRepository loyaltyRepository,
                              CustomerRepository customerRepository,
                              VipProgramRepository vipProgramRepository,
                              VipHistoryRepository vipHistoryRepository,
                              RewardRuleRepository rewardRuleRepository,
                              BirthdayNotificationLogRepository birthdayNotificationLogRepository) {
        this.loyaltyRepository = loyaltyRepository;
        this.customerRepository = customerRepository;
        this.vipProgramRepository = vipProgramRepository;
        this.vipHistoryRepository = vipHistoryRepository;
        this.rewardRuleRepository = rewardRuleRepository;
        this.birthdayNotificationLogRepository = birthdayNotificationLogRepository;
    }

    @Override
    public void addPoint(AddPointRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        int currentPoint = customer.getDiemTichLuy() == null ? 0 : customer.getDiemTichLuy();
        int addPoint = request.getPoints() == null ? 0 : request.getPoints();

        String oldLevel = customer.getLoaiKhach();
        customer.setDiemTichLuy(currentPoint + addPoint);
        updateVipLevel(customer, oldLevel, "Cộng điểm");

        customerRepository.save(customer);

        LoyaltyPointHistory history = new LoyaltyPointHistory();
        history.setCustomer(customer);
        history.setPoints(addPoint);
        history.setType("ADD");
        history.setNote(request.getNote());
        loyaltyRepository.save(history);
    }

    @Override
    public void deductPoint(DeductPointRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        int currentPoint = customer.getDiemTichLuy() == null ? 0 : customer.getDiemTichLuy();
        int deductPoint = request.getPoints() == null ? 0 : request.getPoints();
        int newPoint = Math.max(currentPoint - deductPoint, 0);

        String oldLevel = customer.getLoaiKhach();
        customer.setDiemTichLuy(newPoint);
        updateVipLevel(customer, oldLevel, "Trừ điểm");

        customerRepository.save(customer);

        LoyaltyPointHistory history = new LoyaltyPointHistory();
        history.setCustomer(customer);
        history.setPoints(deductPoint);
        history.setType("SUBTRACT");
        history.setNote(request.getNote());
        loyaltyRepository.save(history);
    }

    @Override
    public List<LoyaltyHistoryResponse> getHistory(Integer customerId) {
        return loyaltyRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapHistory)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoyaltyCustomerResponse> getAllLoyaltyCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customer -> {
                    LoyaltyCustomerResponse response = new LoyaltyCustomerResponse();
                    response.setCustomerId(customer.getId());
                    response.setTen(customer.getTen());
                    response.setEmail(customer.getEmail());
                    response.setSdt(customer.getSdt());
                    response.setDiemTichLuy(customer.getDiemTichLuy());
                    response.setLoaiKhach(customer.getLoaiKhach());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<VipProgramResponse> getVipPrograms() {
        return vipProgramRepository.findByIsActiveTrueOrderByMinPointsAsc()
                .stream()
                .map(this::mapVipProgram)
                .collect(Collectors.toList());
    }

    @Override
    public List<VipHistoryResponse> getVipHistory(Integer customerId) {
        return vipHistoryRepository.findByCustomerIdOrderByChangedAtDesc(customerId)
                .stream()
                .map(this::mapVipHistory)
                .collect(Collectors.toList());
    }

    @Override
    public List<RewardRuleResponse> getRewardRules() {
        return rewardRuleRepository.findByIsActiveTrueOrderByRequiredPointsAsc()
                .stream()
                .map(this::mapRewardRule)
                .collect(Collectors.toList());
    }

    @Override
    public List<BirthdayNotificationResponse> getBirthdayLogs(Integer customerId) {
        return birthdayNotificationLogRepository.findByCustomerIdOrderBySentDateDesc(customerId)
                .stream()
                .map(this::mapBirthdayLog)
                .collect(Collectors.toList());
    }

    private void updateVipLevel(Customer customer, String oldLevel, String reason) {
        String newLevel = determineVipLevel(customer.getDiemTichLuy());

        if (newLevel == null) {
            newLevel = "THUONG";
        }

        customer.setLoaiKhach(newLevel.equals("VIP") ? "VIP" : "THUONG");

        if (oldLevel == null || !oldLevel.equals(newLevel)) {
            VipHistory vipHistory = new VipHistory();
            vipHistory.setCustomer(customer);
            vipHistory.setOldLevel(oldLevel);
            vipHistory.setNewLevel(newLevel);
            vipHistory.setReason(reason);
            vipHistoryRepository.save(vipHistory);
        }
    }

    private String determineVipLevel(Integer points) {
        if (points == null) return "THUONG";

        List<VipProgram> programs = vipProgramRepository.findByIsActiveTrueOrderByMinPointsAsc();
        String current = "THUONG";

        for (VipProgram program : programs) {
            if (points >= program.getMinPoints()) {
                current = program.getLevelName();
            }
        }
        return current;
    }

    private LoyaltyHistoryResponse mapHistory(LoyaltyPointHistory history) {
        LoyaltyHistoryResponse response = new LoyaltyHistoryResponse();
        response.setId(history.getId());
        response.setCustomerId(history.getCustomer().getId());
        response.setPoints(history.getPoints());
        response.setType(history.getType());
        response.setNote(history.getNote());
        response.setCreatedAt(history.getCreatedAt());
        return response;
    }

    private VipProgramResponse mapVipProgram(VipProgram program) {
        VipProgramResponse response = new VipProgramResponse();
        response.setId(program.getId());
        response.setLevelName(program.getLevelName());
        response.setMinPoints(program.getMinPoints());
        response.setMinSpending(program.getMinSpending());
        response.setBenefit(program.getBenefit());
        response.setIsActive(program.getIsActive());
        return response;
    }

    private VipHistoryResponse mapVipHistory(VipHistory history) {
        VipHistoryResponse response = new VipHistoryResponse();
        response.setId(history.getId());
        response.setCustomerId(history.getCustomer().getId());
        response.setOldLevel(history.getOldLevel());
        response.setNewLevel(history.getNewLevel());
        response.setReason(history.getReason());
        response.setChangedAt(history.getChangedAt());
        return response;
    }

    private RewardRuleResponse mapRewardRule(RewardRule rule) {
        RewardRuleResponse response = new RewardRuleResponse();
        response.setId(rule.getId());
        response.setRewardName(rule.getRewardName());
        response.setRequiredPoints(rule.getRequiredPoints());
        response.setDiscountValue(rule.getDiscountValue());
        response.setDescription(rule.getDescription());
        response.setIsActive(rule.getIsActive());
        return response;
    }

    private BirthdayNotificationResponse mapBirthdayLog(BirthdayNotificationLog log) {
        BirthdayNotificationResponse response = new BirthdayNotificationResponse();
        response.setId(log.getId());
        response.setCustomerId(log.getCustomer().getId());
        response.setTen(log.getCustomer().getTen());
        response.setChannel(log.getChannel());
        response.setNote(log.getNote());
        response.setSentDate(log.getSentDate());
        return response;
    }
}