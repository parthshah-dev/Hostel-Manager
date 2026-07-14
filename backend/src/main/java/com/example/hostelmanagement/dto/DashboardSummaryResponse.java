package com.example.hostelmanagement.dto;

import java.math.BigDecimal;


public record DashboardSummaryResponse(
    long totalRooms,
    long occupiedRooms,
    long vacantRooms,
    long totalTenants,
    BigDecimal pendingRent,
    BigDecimal monthlyRevenue
) {}
