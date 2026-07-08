package com.example.hostelmanagement.dto;

import java.math.BigDecimal;

/**
 * DTO record representing total pending/unpaid balance metrics.
 */
public record PendingAmountResponse(
    BigDecimal pendingAmount
) {}
