package com.example.hostelmanagement.dto;

import java.math.BigDecimal;


public record MonthlyRevenueResponse(
    String month,
    BigDecimal totalCollected
) {}
