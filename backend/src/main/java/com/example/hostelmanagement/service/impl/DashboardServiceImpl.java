package com.example.hostelmanagement.service.impl;

import com.example.hostelmanagement.dto.DashboardSummaryResponse;
import com.example.hostelmanagement.entity.RoomStatus;
import com.example.hostelmanagement.entity.PaymentStatus;
import com.example.hostelmanagement.entity.User;
import com.example.hostelmanagement.repository.RentRepository;
import com.example.hostelmanagement.repository.RoomRepository;
import com.example.hostelmanagement.repository.TenantRepository;
import com.example.hostelmanagement.security.AuthenticationHelper;
import com.example.hostelmanagement.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;
    private final RentRepository rentRepository;
    private final AuthenticationHelper authenticationHelper;

    public DashboardServiceImpl(
            RoomRepository roomRepository,
            TenantRepository tenantRepository,
            RentRepository rentRepository,
            AuthenticationHelper authenticationHelper
    ) {
        this.roomRepository = roomRepository;
        this.tenantRepository = tenantRepository;
        this.rentRepository = rentRepository;
        this.authenticationHelper = authenticationHelper;
    }

    @Override
    public DashboardSummaryResponse getDashboardSummary() {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Generating dashboard summary metrics for admin {}", currentAdmin.getEmail());
        try {
            long totalRooms = roomRepository.countByAdmin(currentAdmin);
            long occupiedRooms = roomRepository.countByRoomStatusAndAdmin(RoomStatus.OCCUPIED, currentAdmin);
            long vacantRooms = roomRepository.countByRoomStatusAndAdmin(RoomStatus.AVAILABLE, currentAdmin);
            long totalTenants = tenantRepository.countByActiveTrueAndRoomAdmin(currentAdmin);

            BigDecimal pendingRent = rentRepository.sumByPaymentStatusAndAdmin(PaymentStatus.PENDING, currentAdmin);
            if (pendingRent == null) {
                pendingRent = BigDecimal.ZERO;
            }

            String currentMonthStr = DateTimeFormatter.ofPattern("yyyy-MM").format(LocalDate.now());
            BigDecimal monthlyRevenue = rentRepository.sumPaidAmountForCurrentMonthAndAdmin(currentMonthStr, currentAdmin);
            if (monthlyRevenue == null) {
                monthlyRevenue = BigDecimal.ZERO;
            }

            log.info("Dashboard summary generated successfully for admin {}: totalRooms={}, occupiedRooms={}, vacantRooms={}, totalTenants={}, pendingRent={}, monthlyRevenue={}",
                    currentAdmin.getEmail(), totalRooms, occupiedRooms, vacantRooms, totalTenants, pendingRent, monthlyRevenue);

            return new DashboardSummaryResponse(
                    totalRooms,
                    occupiedRooms,
                    vacantRooms,
                    totalTenants,
                    pendingRent,
                    monthlyRevenue
            );
        } catch (Exception e) {
            log.error("Error occurred while generating dashboard summary for admin {}. Falling back to zero values.", currentAdmin.getEmail(), e);

            return new DashboardSummaryResponse(
                    0, 0, 0, 0,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }
    }
}
