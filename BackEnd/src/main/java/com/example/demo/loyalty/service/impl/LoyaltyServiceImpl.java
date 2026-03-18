package com.example.demo.loyalty.service.impl;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.loyalty.dto.*;
import com.example.demo.loyalty.entity.*;
import com.example.demo.loyalty.repository.*;
import com.example.demo.loyalty.service.LoyaltyService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoyaltyServiceImpl implements LoyaltyService {

    private final CustomerRepository customerRepository;
    private final LoyaltyRepository loyaltyRepository;
    private final VipProgramRepository vipProgramRepository;
    private final VipHistoryRepository vipHistoryRepository;
    private final RewardRuleRepository rewardRuleRepository;
    private final BirthdayNotificationLogRepository birthdayRepository;

    public LoyaltyServiceImpl(
            CustomerRepository customerRepository,
            LoyaltyRepository loyaltyRepository,
            VipProgramRepository vipProgramRepository,
            VipHistoryRepository vipHistoryRepository,
            RewardRuleRepository rewardRuleRepository,
            BirthdayNotificationLogRepository birthdayRepository) {

        this.customerRepository = customerRepository;
        this.loyaltyRepository = loyaltyRepository;
        this.vipProgramRepository = vipProgramRepository;
        this.vipHistoryRepository = vipHistoryRepository;
        this.rewardRuleRepository = rewardRuleRepository;
        this.birthdayRepository = birthdayRepository;
    }

    @Override
    public void addPoint(AddPointRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow();

        customer.setDiemTichLuy(customer.getDiemTichLuy() + request.getPoints());

        customerRepository.save(customer);

        LoyaltyPointHistory history = new LoyaltyPointHistory();

        history.setCustomerId(customer.getId());
        history.setPoints(request.getPoints());
        history.setType("ADD");
        history.setNote(request.getNote());
        history.setCreatedAt(LocalDateTime.now());
        loyaltyRepository.save(history);

    }

    @Override
    public void deductPoint(DeductPointRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow();

        customer.setDiemTichLuy(customer.getDiemTichLuy() - request.getPoints());

        customerRepository.save(customer);

        LoyaltyPointHistory history = new LoyaltyPointHistory();

        history.setCustomerId(customer.getId());
        history.setPoints(request.getPoints());
        history.setType("SUBTRACT");
        history.setNote(request.getNote());
        history.setCreatedAt(LocalDateTime.now());
        loyaltyRepository.save(history);

    }

    @Override
    public List<LoyaltyHistoryResponse> getHistory(Integer customerId) {

        return loyaltyRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(h -> {

                    LoyaltyHistoryResponse r = new LoyaltyHistoryResponse();

                    r.setId(h.getId());
                    r.setPoints(h.getPoints());
                    r.setType(h.getType());
                    r.setNote(h.getNote());
                    r.setCreatedAt(h.getCreatedAt());

                    return r;

                }).collect(Collectors.toList());

    }

    @Override
    public List<LoyaltyCustomerResponse> getAllLoyaltyCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(c -> {

                    LoyaltyCustomerResponse r = new LoyaltyCustomerResponse();

                    r.setCustomerId(c.getId());
                    r.setTen(c.getTen());
                    r.setEmail(c.getEmail());
                    r.setSdt(c.getSdt());
                    r.setDiemTichLuy(c.getDiemTichLuy());
                    r.setLoaiKhach(c.getLoaiKhach());

                    return r;

                }).collect(Collectors.toList());

    }

    @Override
    public List<VipProgramResponse> getVipPrograms() {

        return vipProgramRepository.findAll()
                .stream()
                .map(v -> {

                    VipProgramResponse r = new VipProgramResponse();

                    r.setId(v.getId());
                    r.setLevelName(v.getLevelName());
                    r.setMinPoints(v.getMinPoints());
                    r.setMinSpending(v.getMinSpending());
                    r.setBenefit(v.getBenefit());
                    r.setIsActive(v.getIsActive());

                    return r;

                }).collect(Collectors.toList());

    }

    @Override
    public List<VipHistoryResponse> getVipHistory(Integer customerId) {

        return vipHistoryRepository.findByCustomerIdOrderByChangedAtDesc(customerId)
                .stream()
                .map(v -> {

                    VipHistoryResponse r = new VipHistoryResponse();

                    r.setOldLevel(v.getOldLevel());
                    r.setNewLevel(v.getNewLevel());
                    r.setReason(v.getReason());
                    r.setChangedAt(v.getChangedAt());

                    return r;

                }).collect(Collectors.toList());

    }

    @Override
    public List<RewardRuleResponse> getRewardRules() {

        return rewardRuleRepository.findAll()
                .stream()
                .map(r -> {

                    RewardRuleResponse res = new RewardRuleResponse();

                    res.setId(r.getId());
                    res.setRewardName(r.getRewardName());
                    res.setRequiredPoints(r.getRequiredPoints());
                    res.setDiscountValue(r.getDiscountValue());
                    res.setDescription(r.getDescription());
                    res.setIsActive(r.getIsActive());

                    return res;

                }).collect(Collectors.toList());

    }

    @Override
    public List<BirthdayNotificationResponse> getBirthdayLogs(Integer customerId) {

        return birthdayRepository.findByCustomerId(customerId)
                .stream()
                .map(b -> {

                    BirthdayNotificationResponse r = new BirthdayNotificationResponse();

                    r.setId(b.getId());
                    r.setCustomerId(b.getCustomerId());
                    r.setSentDate(b.getSentDate());
                    r.setChannel(b.getChannel());
                    r.setNote(b.getNote());

                    return r;

                }).collect(Collectors.toList());

    }

    @Override
    public void assignVip(AssignVipRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow();

        String old = customer.getLoaiKhach();

        customer.setLoaiKhach(request.getNewLevel());

        customerRepository.save(customer);

        VipHistory history = new VipHistory();

        history.setCustomerId(customer.getId());
        history.setOldLevel(old);
        history.setNewLevel(request.getNewLevel());
        history.setReason(request.getReason());
        history.setChangedAt(LocalDateTime.now());

        vipHistoryRepository.save(history);

    }

    @Override
    public void createVipProgram(VipProgramRequest request) {

        VipProgram vip = new VipProgram();

        vip.setLevelName(request.getLevelName());
        vip.setMinPoints(request.getMinPoints());
        vip.setMinSpending(request.getMinSpending());
        vip.setBenefit(request.getBenefit());
        vip.setIsActive(request.getIsActive());

        vipProgramRepository.save(vip);

    }

    @Override
    public void createRewardRule(RewardRuleRequest request) {

        RewardRule r = new RewardRule();

        r.setRewardName(request.getRewardName());
        r.setRequiredPoints(request.getRequiredPoints());
        r.setDiscountValue(request.getDiscountValue());
        r.setDescription(request.getDescription());
        r.setIsActive(request.getIsActive());

        rewardRuleRepository.save(r);

    }

}