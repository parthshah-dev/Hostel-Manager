package com.example.hostelmanagement.service.impl;

import com.example.hostelmanagement.dto.*;
import com.example.hostelmanagement.entity.*;
import com.example.hostelmanagement.exception.*;
import com.example.hostelmanagement.repository.RentRepository;
import com.example.hostelmanagement.repository.TenantRepository;
import com.example.hostelmanagement.service.RentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation containing transaction boundaries and business rules for Rent ledgers.
 */
@Service
@Slf4j
public class RentServiceImpl implements RentService {

    private final RentRepository rentRepository;
    private final TenantRepository tenantRepository;
    private final com.example.hostelmanagement.service.EmailService emailService;

    public RentServiceImpl(
            RentRepository rentRepository,
            TenantRepository tenantRepository,
            com.example.hostelmanagement.service.EmailService emailService
    ) {
        this.rentRepository = rentRepository;
        this.tenantRepository = tenantRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public ApiResponse generateRent(GenerateRentRequest request) {
        log.info("Generating rent records for month: {}", request.rentMonth());

        List<Tenant> activeTenants = tenantRepository.findByActiveTrue();
        if (activeTenants.isEmpty()) {
            return new ApiResponse("No active tenants found. No rent records generated.");
        }

        int generatedCount = 0;
        for (Tenant tenant : activeTenants) {
            boolean exists = rentRepository.existsByTenantIdAndRentMonth(tenant.getId(), request.rentMonth());
            if (exists) {
                log.info("Rent record already exists for tenant: {}, month: {}. Skipping.", tenant.getFullName(), request.rentMonth());
                continue;
            }

            Room room = tenant.getRoom();
            BigDecimal monthlyRent = room.getMonthlyRent();

            Rent rent = Rent.builder()
                    .tenant(tenant)
                    .room(room)
                    .rentMonth(request.rentMonth())
                    .monthlyRent(monthlyRent)
                    .amountPaid(BigDecimal.ZERO)
                    .dueAmount(monthlyRent)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build();

            rentRepository.save(rent);
            generatedCount++;
        }

        log.info("Monthly rent generated successfully. Records created: {}", generatedCount);
        return new ApiResponse("Monthly rent generated successfully.");
    }

    @Override
    @Transactional
    public ApiResponse payRent(Long rentId, RentPaymentRequest request) {
        log.info("Recording payment for Rent ID: {}", rentId);

        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new RentNotFoundException("Rent record not found with ID: " + rentId));

        if (rent.getPaymentStatus() == PaymentStatus.PAID) {
            log.warn("Payment block: Rent record {} already paid", rentId);
            throw new RentAlreadyPaidException("Rent is already paid.");
        }

        rent.setAmountPaid(rent.getMonthlyRent());
        rent.setDueAmount(BigDecimal.ZERO);
        rent.setPaymentStatus(PaymentStatus.PAID);
        rent.setPaymentDate(LocalDate.now());
        rent.setPaymentMode(request.paymentMode());
        rent.setRemarks(request.remarks());

        rentRepository.save(rent);
        log.info("Successfully recorded payment for Rent ID: {}", rentId);

        try {
            Tenant tenant = rent.getTenant();
            if (tenant != null) {
                emailService.sendPaymentConfirmationEmail(
                        tenant.getEmail(),
                        tenant.getFullName(),
                        rent.getRentMonth(),
                        rent.getAmountPaid(),
                        rent.getPaymentMode().name(),
                        rent.getPaymentDate()
                );
            }
        } catch (Exception e) {
            log.error("Failed to send payment confirmation email for rent ID: {}", rentId, e);
        }

        return new ApiResponse("Rent payment recorded successfully.");
    }

    @Override
    public List<RentResponse> getPendingRents() {
        log.info("Retrieving all pending rents");
        return rentRepository.findByPaymentStatusOrderByRentMonthDesc(PaymentStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentResponse> getRentHistory() {
        log.info("Retrieving all rent records sorted by newest month first");
        return rentRepository.findAllByOrderByRentMonthDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentResponse> getTenantRentHistory(Long tenantId) {
        log.info("Retrieving rent history for tenant ID: {}", tenantId);
        if (!tenantRepository.existsById(tenantId)) {
            throw new TenantNotFoundException("Tenant not found with ID: " + tenantId);
        }
        return rentRepository.findByTenantId(tenantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RentResponse getRentById(Long id) {
        log.info("Retrieving rent by ID: {}", id);
        Rent rent = rentRepository.findById(id)
                .orElseThrow(() -> new RentNotFoundException("Rent record not found with ID: " + id));
        return mapToResponse(rent);
    }

    @Override
    public MonthlyRevenueResponse getMonthlyRevenue(String month) {
        log.info("Retrieving monthly revenue for: {}", month);
        BigDecimal totalCollected = rentRepository.sumPaidAmountForCurrentMonth(month);
        if (totalCollected == null) {
            totalCollected = BigDecimal.ZERO;
        }
        return new MonthlyRevenueResponse(month, totalCollected);
    }

    @Override
    public PendingAmountResponse getPendingAmount() {
        log.info("Retrieving total pending due amount");
        BigDecimal pending = rentRepository.sumByPaymentStatus(PaymentStatus.PENDING);
        if (pending == null) {
            pending = BigDecimal.ZERO;
        }
        return new PendingAmountResponse(pending);
    }

    private RentResponse mapToResponse(Rent rent) {
        return new RentResponse(
                rent.getId(),
                rent.getTenant().getId(),
                rent.getTenant().getFullName(),
                rent.getRoom().getRoomNumber(),
                rent.getRentMonth(),
                rent.getMonthlyRent(),
                rent.getAmountPaid(),
                rent.getDueAmount(),
                rent.getPaymentStatus(),
                rent.getPaymentDate(),
                rent.getPaymentMode(),
                rent.getRemarks(),
                rent.getCreatedAt(),
                rent.getUpdatedAt()
        );
    }
}
