package com.example.hostelmanagement.service.impl;

import com.example.hostelmanagement.dto.*;
import com.example.hostelmanagement.entity.*;
import com.example.hostelmanagement.exception.*;
import com.example.hostelmanagement.repository.RentRepository;
import com.example.hostelmanagement.repository.TenantRepository;
import com.example.hostelmanagement.security.AuthenticationHelper;
import com.example.hostelmanagement.service.RentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class RentServiceImpl implements RentService {

    private final RentRepository rentRepository;
    private final TenantRepository tenantRepository;
    private final com.example.hostelmanagement.service.EmailService emailService;
    private final AuthenticationHelper authenticationHelper;

    public RentServiceImpl(
            RentRepository rentRepository,
            TenantRepository tenantRepository,
            com.example.hostelmanagement.service.EmailService emailService,
            AuthenticationHelper authenticationHelper
    ) {
        this.rentRepository = rentRepository;
        this.tenantRepository = tenantRepository;
        this.emailService = emailService;
        this.authenticationHelper = authenticationHelper;
    }

    @Override
    @Transactional
    public ApiResponse generateRent(GenerateRentRequest request) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Generating rent records for month: {} by admin {}", request.rentMonth(), currentAdmin.getEmail());

        List<Tenant> activeTenants = tenantRepository.findByActiveTrueAndRoomAdmin(currentAdmin);
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
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Recording payment for Rent ID: {} by admin {}", rentId, currentAdmin.getEmail());

        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new RentNotFoundException("Rent record not found with ID: " + rentId));

        if (!rent.getRoom().getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Rent ID {} does not belong to admin {}", rentId, currentAdmin.getEmail());
            throw new RentNotFoundException("Rent record not found with ID: " + rentId);
        }

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
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving all pending rents for admin {}", currentAdmin.getEmail());
        return rentRepository.findByPaymentStatusAndRoomAdminOrderByRentMonthDesc(PaymentStatus.PENDING, currentAdmin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentResponse> getRentHistory() {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving all rent records sorted by newest month first for admin {}", currentAdmin.getEmail());
        return rentRepository.findByRoomAdminOrderByRentMonthDesc(currentAdmin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentResponse> getTenantRentHistory(Long tenantId) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving rent history for tenant ID: {} by admin {}", tenantId, currentAdmin.getEmail());

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + tenantId));

        if (!tenant.getRoom().getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Tenant ID {} does not belong to admin {}", tenantId, currentAdmin.getEmail());
            throw new TenantNotFoundException("Tenant not found with ID: " + tenantId);
        }

        return rentRepository.findByTenantId(tenantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RentResponse getRentById(Long id) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving rent by ID: {} for admin {}", id, currentAdmin.getEmail());
        Rent rent = rentRepository.findById(id)
                .orElseThrow(() -> new RentNotFoundException("Rent record not found with ID: " + id));

        if (!rent.getRoom().getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Rent ID {} does not belong to admin {}", id, currentAdmin.getEmail());
            throw new RentNotFoundException("Rent record not found with ID: " + id);
        }

        return mapToResponse(rent);
    }

    @Override
    public MonthlyRevenueResponse getMonthlyRevenue(String month) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving monthly revenue for: {} for admin {}", month, currentAdmin.getEmail());
        BigDecimal totalCollected = rentRepository.sumPaidAmountForCurrentMonthAndAdmin(month, currentAdmin);
        if (totalCollected == null) {
            totalCollected = BigDecimal.ZERO;
        }
        return new MonthlyRevenueResponse(month, totalCollected);
    }

    @Override
    public PendingAmountResponse getPendingAmount() {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving total pending due amount for admin {}", currentAdmin.getEmail());
        BigDecimal pending = rentRepository.sumByPaymentStatusAndAdmin(PaymentStatus.PENDING, currentAdmin);
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
