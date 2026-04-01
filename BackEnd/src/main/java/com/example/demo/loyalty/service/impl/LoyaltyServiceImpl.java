package com.example.demo.loyalty.service.impl;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.loyalty.dto.*;
import com.example.demo.loyalty.entity.*;
import com.example.demo.loyalty.repository.*;
import com.example.demo.loyalty.service.LoyaltyService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
            BirthdayNotificationLogRepository birthdayRepository
    ) {
        this.customerRepository = customerRepository;
        this.loyaltyRepository = loyaltyRepository;
        this.vipProgramRepository = vipProgramRepository;
        this.vipHistoryRepository = vipHistoryRepository;
        this.rewardRuleRepository = rewardRuleRepository;
        this.birthdayRepository = birthdayRepository;
    }

    @Override
    public void addPoint(AddPointRequest request) {
        if (request == null || request.getCustomerId() == null || request.getPoints() == null || request.getPoints() <= 0) {
            throw new RuntimeException("Dữ liệu cộng điểm không hợp lệ");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        customer.setDiemTichLuy(safePoints(customer.getDiemTichLuy()) + request.getPoints());
        customerRepository.save(customer);

        LoyaltyPointHistory history = new LoyaltyPointHistory();
        history.setCustomerId(customer.getId());
        history.setPoints(request.getPoints());
        history.setType("ADD");
        history.setNote(request.getNote());
        history.setCreatedAt(LocalDateTime.now());
        loyaltyRepository.save(history);

        autoRecalculateVip(customer, "Cộng điểm thủ công");
    }

    @Override
    public void deductPoint(DeductPointRequest request) {
        if (request == null || request.getCustomerId() == null || request.getPoints() == null || request.getPoints() <= 0) {
            throw new RuntimeException("Dữ liệu trừ điểm không hợp lệ");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        int current = safePoints(customer.getDiemTichLuy());
        int next = Math.max(0, current - request.getPoints());

        customer.setDiemTichLuy(next);
        customerRepository.save(customer);

        LoyaltyPointHistory history = new LoyaltyPointHistory();
        history.setCustomerId(customer.getId());
        history.setPoints(request.getPoints());
        history.setType("SUBTRACT");
        history.setNote(request.getNote());
        history.setCreatedAt(LocalDateTime.now());
        loyaltyRepository.save(history);

        autoRecalculateVip(customer, "Trừ điểm thủ công");
    }

    @Override
    public List<LoyaltyHistoryResponse> getHistory(Integer customerId) {
        return loyaltyRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapHistory)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoyaltyHistoryResponse> filterHistory(LoyaltyHistoryFilterRequest request) {
        if (request == null) {
            return loyaltyRepository.findAll()
                    .stream()
                    .sorted(Comparator.comparing(LoyaltyPointHistory::getCreatedAt).reversed())
                    .map(this::mapHistory)
                    .collect(Collectors.toList());
        }

        String normalizedType = request.getType() == null || request.getType().isBlank()
                ? null
                : request.getType().trim().toUpperCase();

        return loyaltyRepository.filterHistory(
                request.getCustomerId(),
                normalizedType,
                request.getFromDate(),
                request.getToDate()
        )
                .stream()
                .map(this::mapHistory)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoyaltyCustomerResponse> getAllLoyaltyCustomers() {
        return customerRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Customer::getDiemTichLuy,
                        Comparator.nullsLast(Integer::compareTo)).reversed())
                .map(c -> {
                    LoyaltyCustomerResponse r = new LoyaltyCustomerResponse();
                    r.setCustomerId(c.getId());
                    r.setTen(c.getTen());
                    r.setEmail(c.getEmail());
                    r.setSdt(c.getSdt());
                    r.setDiemTichLuy(c.getDiemTichLuy());
                    r.setLoaiKhach(c.getLoaiKhach());
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<VipProgramResponse> getVipPrograms() {
        return vipProgramRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(VipProgram::getMinPoints))
                .map(v -> {
                    VipProgramResponse r = new VipProgramResponse();
                    r.setId(v.getId());
                    r.setLevelName(v.getLevelName());
                    r.setMinPoints(v.getMinPoints());
                    r.setMinSpending(v.getMinSpending());
                    r.setBenefit(v.getBenefit());
                    r.setIsActive(v.getIsActive());
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<VipHistoryResponse> getVipHistory(Integer customerId) {
        return vipHistoryRepository.findByCustomerIdOrderByChangedAtDesc(customerId)
                .stream()
                .map(v -> {
                    VipHistoryResponse r = new VipHistoryResponse();
                    r.setId(v.getId());
                    r.setCustomerId(v.getCustomerId());
                    r.setOldLevel(v.getOldLevel());
                    r.setNewLevel(v.getNewLevel());
                    r.setReason(v.getReason());
                    r.setChangedAt(v.getChangedAt());
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RewardRuleResponse> getRewardRules() {
        return rewardRuleRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(RewardRule::getRequiredPoints))
                .map(this::mapRewardRule)
                .collect(Collectors.toList());
    }

    @Override
    public List<BirthdayNotificationResponse> getBirthdayLogs(Integer customerId) {
        return birthdayRepository.findByCustomerId(customerId)
                .stream()
                .sorted(Comparator.comparing(BirthdayNotificationLog::getSentDate).reversed())
                .map(log -> {
                    BirthdayNotificationResponse r = new BirthdayNotificationResponse();
                    r.setId(log.getId());
                    r.setCustomerId(log.getCustomerId());
                    r.setTen(customerRepository.findById(log.getCustomerId()).map(Customer::getTen).orElse(null));
                    r.setSentDate(log.getSentDate());
                    r.setChannel(log.getChannel());
                    r.setNote(log.getNote());
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void assignVip(AssignVipRequest request) {
        if (request == null || request.getCustomerId() == null || request.getNewLevel() == null || request.getNewLevel().isBlank()) {
            throw new RuntimeException("Dữ liệu gán VIP không hợp lệ");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        String old = customer.getLoaiKhach();
        String next = request.getNewLevel().trim().toUpperCase();

        customer.setLoaiKhach(next);
        customerRepository.save(customer);

        VipHistory history = new VipHistory();
        history.setCustomerId(customer.getId());
        history.setOldLevel(old);
        history.setNewLevel(next);
        history.setReason(request.getReason());
        history.setChangedAt(LocalDateTime.now());
        vipHistoryRepository.save(history);
    }

    @Override
    public void createVipProgram(VipProgramRequest request) {
        validateVipProgramRequest(request);

        if (vipProgramRepository.existsByLevelNameIgnoreCase(request.getLevelName().trim())) {
            throw new RuntimeException("Level VIP đã tồn tại");
        }

        VipProgram vip = new VipProgram();
        vip.setLevelName(request.getLevelName().trim().toUpperCase());
        vip.setMinPoints(request.getMinPoints());
        vip.setMinSpending(request.getMinSpending());
        vip.setBenefit(request.getBenefit());
        vip.setIsActive(request.getIsActive() == null ? true : request.getIsActive());

        vipProgramRepository.save(vip);
    }

    @Override
    public VipProgramResponse updateVipProgram(Integer id, VipProgramRequest request) {
        validateVipProgramRequest(request);

        VipProgram vip = vipProgramRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy VIP program"));

        String nextLevel = request.getLevelName().trim().toUpperCase();
        boolean duplicate = vipProgramRepository.findAll().stream()
                .anyMatch(item -> !item.getId().equals(id)
                        && item.getLevelName() != null
                        && item.getLevelName().equalsIgnoreCase(nextLevel));

        if (duplicate) {
            throw new RuntimeException("Level VIP đã tồn tại");
        }

        vip.setLevelName(nextLevel);
        vip.setMinPoints(request.getMinPoints());
        vip.setMinSpending(request.getMinSpending());
        vip.setBenefit(request.getBenefit());
        vip.setIsActive(request.getIsActive() == null ? vip.getIsActive() : request.getIsActive());

        return toVipProgramResponse(vipProgramRepository.save(vip));
    }

    @Override
    public void createRewardRule(RewardRuleRequest request) {
        validateRewardRuleRequest(request);

        if (rewardRuleRepository.existsByRewardNameIgnoreCase(request.getRewardName().trim())) {
            throw new RuntimeException("Reward rule đã tồn tại");
        }

        RewardRule r = new RewardRule();
        r.setRewardName(request.getRewardName().trim());
        r.setRequiredPoints(request.getRequiredPoints());
        r.setDiscountValue(request.getDiscountValue());
        r.setDescription(request.getDescription());
        r.setIsActive(request.getIsActive() == null ? true : request.getIsActive());

        rewardRuleRepository.save(r);
    }

    @Override
    public RewardRuleResponse updateRewardRule(Integer id, RewardRuleRequest request) {
        validateRewardRuleRequest(request);

        RewardRule rule = rewardRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reward rule"));

        boolean duplicate = rewardRuleRepository.findAll().stream()
                .anyMatch(item -> !item.getId().equals(id)
                        && item.getRewardName() != null
                        && item.getRewardName().equalsIgnoreCase(request.getRewardName().trim()));

        if (duplicate) {
            throw new RuntimeException("Reward rule đã tồn tại");
        }

        rule.setRewardName(request.getRewardName().trim());
        rule.setRequiredPoints(request.getRequiredPoints());
        rule.setDiscountValue(request.getDiscountValue());
        rule.setDescription(request.getDescription());
        rule.setIsActive(request.getIsActive() == null ? rule.getIsActive() : request.getIsActive());

        return mapRewardRule(rewardRuleRepository.save(rule));
    }

    @Override
    public RedeemRewardResponse redeemReward(RedeemRewardRequest request) {
        if (request == null || request.getCustomerId() == null || request.getRewardRuleId() == null) {
            throw new RuntimeException("Dữ liệu redeem không hợp lệ");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        RewardRule rule = rewardRuleRepository.findById(request.getRewardRuleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reward rule"));

        if (!Boolean.TRUE.equals(rule.getIsActive())) {
            throw new RuntimeException("Reward rule đang bị tắt");
        }

        int currentPoints = safePoints(customer.getDiemTichLuy());
        if (currentPoints < rule.getRequiredPoints()) {
            throw new RuntimeException("Khách hàng không đủ điểm để đổi ưu đãi");
        }

        int remaining = currentPoints - rule.getRequiredPoints();
        customer.setDiemTichLuy(remaining);
        customerRepository.save(customer);

        LoyaltyPointHistory history = new LoyaltyPointHistory();
        history.setCustomerId(customer.getId());
        history.setPoints(rule.getRequiredPoints());
        history.setType("SUBTRACT");
        history.setNote(buildRedeemNote(rule, request.getNote()));
        history.setCreatedAt(LocalDateTime.now());
        loyaltyRepository.save(history);

        autoRecalculateVip(customer, "Redeem reward " + rule.getRewardName());

        RedeemRewardResponse response = new RedeemRewardResponse();
        response.setCustomerId(customer.getId());
        response.setCustomerName(customer.getTen());
        response.setRewardRuleId(rule.getId());
        response.setRewardName(rule.getRewardName());
        response.setRequiredPoints(rule.getRequiredPoints());
        response.setDiscountValue(rule.getDiscountValue());
        response.setRemainingPoints(remaining);
        response.setRedeemedAt(history.getCreatedAt());
        response.setNote(history.getNote());
        return response;
    }

    @Override
    public BirthdayNotificationResponse createBirthdayNotification(BirthdayNotificationRequest request) {
        if (request == null || request.getCustomerId() == null || request.getChannel() == null || request.getChannel().isBlank()) {
            throw new RuntimeException("Dữ liệu birthday notification không hợp lệ");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        if (customer.getNgaySinh() == null) {
            throw new RuntimeException("Khách hàng chưa có ngày sinh");
        }

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);

        String channel = request.getChannel().trim().toUpperCase();

        boolean alreadySentToday = birthdayRepository.existsByCustomerIdAndChannelAndSentDateBetween(
                customer.getId(), channel, start, end
        );

        if (alreadySentToday) {
            throw new RuntimeException("Hôm nay đã gửi thông báo qua kênh này cho khách hàng");
        }

        BirthdayNotificationLog log = new BirthdayNotificationLog();
        log.setCustomerId(customer.getId());
        log.setSentDate(LocalDateTime.now());
        log.setChannel(channel);
        log.setNote(request.getNote());
        birthdayRepository.save(log);

        BirthdayNotificationResponse response = new BirthdayNotificationResponse();
        response.setId(log.getId());
        response.setCustomerId(customer.getId());
        response.setTen(customer.getTen());
        response.setChannel(log.getChannel());
        response.setNote(log.getNote());
        response.setSentDate(log.getSentDate());
        return response;
    }

    @Override
    public List<BirthdayNotificationResponse> getTodayBirthdayCustomers() {
        LocalDate today = LocalDate.now();

        return customerRepository.findAll().stream()
                .filter(c -> c.getNgaySinh() != null
                        && c.getNgaySinh().getDayOfMonth() == today.getDayOfMonth()
                        && c.getNgaySinh().getMonthValue() == today.getMonthValue())
                .map(c -> {
                    BirthdayNotificationResponse r = new BirthdayNotificationResponse();
                    r.setCustomerId(c.getId());
                    r.setTen(c.getTen());
                    r.setNote("Sinh nhật hôm nay");
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BirthdayNotificationResponse> getCurrentMonthBirthdayCustomers() {
        int month = LocalDate.now().getMonthValue();

        return customerRepository.findAll().stream()
                .filter(c -> c.getNgaySinh() != null && c.getNgaySinh().getMonthValue() == month)
                .sorted(Comparator.comparing(c -> c.getNgaySinh().getDayOfMonth()))
                .map(c -> {
                    BirthdayNotificationResponse r = new BirthdayNotificationResponse();
                    r.setCustomerId(c.getId());
                    r.setTen(c.getTen());
                    r.setNote("Sinh nhật trong tháng");
                    return r;
                })
                .collect(Collectors.toList());
    }

    private LoyaltyHistoryResponse mapHistory(LoyaltyPointHistory h) {
        LoyaltyHistoryResponse r = new LoyaltyHistoryResponse();
        r.setId(h.getId());
        r.setCustomerId(h.getCustomerId());
        r.setPoints(h.getPoints());
        r.setType(h.getType());
        r.setNote(h.getNote());
        r.setCreatedAt(h.getCreatedAt());
        return r;
    }

    private RewardRuleResponse mapRewardRule(RewardRule r) {
        RewardRuleResponse res = new RewardRuleResponse();
        res.setId(r.getId());
        res.setRewardName(r.getRewardName());
        res.setRequiredPoints(r.getRequiredPoints());
        res.setDiscountValue(r.getDiscountValue());
        res.setDescription(r.getDescription());
        res.setIsActive(r.getIsActive());
        return res;
    }

    private VipProgramResponse toVipProgramResponse(VipProgram v) {
        VipProgramResponse r = new VipProgramResponse();
        r.setId(v.getId());
        r.setLevelName(v.getLevelName());
        r.setMinPoints(v.getMinPoints());
        r.setMinSpending(v.getMinSpending());
        r.setBenefit(v.getBenefit());
        r.setIsActive(v.getIsActive());
        return r;
    }

    private int safePoints(Integer points) {
        return points == null ? 0 : points;
    }

    private String buildRedeemNote(RewardRule rule, String note) {
        String prefix = "Redeem reward #" + rule.getId() + " - " + rule.getRewardName();
        if (note == null || note.isBlank()) {
            return prefix;
        }
        return prefix + " | " + note.trim();
    }

    private void validateVipProgramRequest(VipProgramRequest request) {
        if (request == null) {
            throw new RuntimeException("Dữ liệu VIP program không hợp lệ");
        }
        if (request.getLevelName() == null || request.getLevelName().isBlank()) {
            throw new RuntimeException("Tên level không được để trống");
        }
        if (request.getMinPoints() == null || request.getMinPoints() < 0) {
            throw new RuntimeException("Min points không hợp lệ");
        }
        if (request.getMinSpending() == null || request.getMinSpending().signum() < 0) {
            throw new RuntimeException("Min spending không hợp lệ");
        }
    }

    private void validateRewardRuleRequest(RewardRuleRequest request) {
        if (request == null) {
            throw new RuntimeException("Dữ liệu reward rule không hợp lệ");
        }
        if (request.getRewardName() == null || request.getRewardName().isBlank()) {
            throw new RuntimeException("Tên ưu đãi không được để trống");
        }
        if (request.getRequiredPoints() == null || request.getRequiredPoints() <= 0) {
            throw new RuntimeException("Required points phải lớn hơn 0");
        }
        if (request.getDiscountValue() == null || request.getDiscountValue().signum() < 0) {
            throw new RuntimeException("Discount value không hợp lệ");
        }
    }

    private void autoRecalculateVip(Customer customer, String reason) {
        List<VipProgram> programs = vipProgramRepository.findByIsActiveTrueOrderByMinPointsAsc();
        if (programs.isEmpty()) {
            return;
        }

        int points = safePoints(customer.getDiemTichLuy());
        String oldLevel = customer.getLoaiKhach();
        String matched = "THUONG";

        for (VipProgram program : programs) {
            int minPoints = program.getMinPoints() == null ? 0 : program.getMinPoints();
            if (points >= minPoints) {
                matched = program.getLevelName();
            }
        }

        if (!Objects.equals(oldLevel, matched)) {
            customer.setLoaiKhach(matched);
            customerRepository.save(customer);

            VipHistory history = new VipHistory();
            history.setCustomerId(customer.getId());
            history.setOldLevel(oldLevel);
            history.setNewLevel(matched);
            history.setReason(reason);
            history.setChangedAt(LocalDateTime.now());
            vipHistoryRepository.save(history);
        }
    }
}