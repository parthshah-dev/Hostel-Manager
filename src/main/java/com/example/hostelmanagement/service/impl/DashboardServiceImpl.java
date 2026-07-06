package com.example.hostelmanagement.service.impl;

import com.example.hostelmanagement.dto.DashboardSummaryResponse;
import com.example.hostelmanagement.entity.RoomStatus;
import com.example.hostelmanagement.entity.PaymentStatus;
import com.example.hostelmanagement.repository.RentRepository;
import com.example.hostelmanagement.repository.RoomRepository;
import com.example.hostelmanagement.repository.TenantRepository;
import com.example.hostelmanagement.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Service implementation for compiling and computing dashboard metrics.
 */
@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;
    private final RentRepository rentRepository;

    public DashboardServiceImpl(
            RoomRepository roomRepository,
            TenantRepository tenantRepository,
            RentRepository rentRepository
    ) {
        this.roomRepository = roomRepository;
        this.tenantRepository = tenantRepository;
        this.rentRepository = rentRepository;
    }

    @Override
    public DashboardSummaryResponse getDashboardSummary() {
        log.info("Generating dashboard summary metrics");
        try {
            long totalRooms = roomRepository.count();
            long occupiedRooms = roomRepository.countByRoomStatus(RoomStatus.OCCUPIED);
            long vacantRooms = roomRepository.countByRoomStatus(RoomStatus.AVAILABLE);
            long totalTenants = tenantRepository.countByActiveTrue();

            BigDecimal pendingRent = rentRepository.sumByPaymentStatus(PaymentStatus.PENDING);
            if (pendingRent == null) {
                pendingRent = BigDecimal.ZERO;
            }

            String currentMonthStr = DateTimeFormatter.ofPattern("yyyy-MM").format(LocalDate.now());
            BigDecimal monthlyRevenue = rentRepository.sumPaidAmountForCurrentMonth(currentMonthStr);
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
