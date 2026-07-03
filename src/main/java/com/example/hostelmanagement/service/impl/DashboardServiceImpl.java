package com.example.hostelmanagement.service.impl;

import com.example.hostelmanagement.dto.DashboardSummaryResponse;
import com.example.hostelmanagement.entity.RoomStatus;
import com.example.hostelmanagement.repository.RentPaymentRepository;
import com.example.hostelmanagement.repository.RoomRepository;
import com.example.hostelmanagement.repository.TenantRepository;
import com.example.hostelmanagement.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * Service implementation for compiling and computing dashboard metrics.
 */
@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;
    private final RentPaymentRepository rentPaymentRepository;

    public DashboardServiceImpl(
            RoomRepository roomRepository,
            TenantRepository tenantRepository,
            RentPaymentRepository rentPaymentRepository
    ) {
        this.roomRepository = roomRepository;
        this.tenantRepository = tenantRepository;
        this.rentPaymentRepository = rentPaymentRepository;
    }

    @Override
    public DashboardSummaryResponse getDashboardSummary() {
        log.info("Generating dashboard summary metrics");
        try {
            long totalRooms = roomRepository.count();
            long occupiedRooms = roomRepository.countByStatus(RoomStatus.OCCUPIED);
            long vacantRooms = roomRepository.countByStatus(RoomStatus.VACANT);
            long totalTenants = tenantRepository.countByActiveTrue();

            BigDecimal pendingRent = rentPaymentRepository.sumUnpaidRent();
            if (pendingRent == null) {
                pendingRent = BigDecimal.ZERO;
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth())
                    .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

            BigDecimal monthlyRevenue = rentPaymentRepository.sumRevenueByMonth(startOfMonth, endOfMonth);
            if (monthlyRevenue == null) {
                monthlyRevenue = BigDecimal.ZERO;
            }

            log.info("Dashboard summary generated successfully: totalRooms={}, occupiedRooms={}, vacantRooms={}, totalTenants={}, pendingRent={}, monthlyRevenue={}",
                    totalRooms, occupiedRooms, vacantRooms, totalTenants, pendingRent, monthlyRevenue);

            return new DashboardSummaryResponse(
                    totalRooms,
                    occupiedRooms,
                    vacantRooms,
                    totalTenants,
                    pendingRent,
                    monthlyRevenue
            );
        } catch (Exception e) {
            log.error("Error occurred while generating dashboard summary. Falling back to zero values.", e);
            // Default everything to zero on failure to prevent breaking client requests
            return new DashboardSummaryResponse(
                    0, 0, 0, 0,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }
    }
}
