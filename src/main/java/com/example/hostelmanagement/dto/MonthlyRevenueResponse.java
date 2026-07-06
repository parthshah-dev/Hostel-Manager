package com.example.hostelmanagement.dto;

import java.math.BigDecimal;

/**
 * DTO record representing monthly collections revenue.
 */
public record MonthlyRevenueResponse(
    String month,
    BigDecimal totalCollected
) {}
