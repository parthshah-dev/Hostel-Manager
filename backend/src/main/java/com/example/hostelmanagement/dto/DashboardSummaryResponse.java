package com.example.hostelmanagement.dto;

import java.math.BigDecimal;

/**
 * DTO representing the summary metrics of the dashboard.
 */
public record DashboardSummaryResponse(
    long totalRooms,
    long occupiedRooms,
    long vacantRooms,
    long totalTenants,
    BigDecimal pendingRent,
    BigDecimal monthlyRevenue
) {}
