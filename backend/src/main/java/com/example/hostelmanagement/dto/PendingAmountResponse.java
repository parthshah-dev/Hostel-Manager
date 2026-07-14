package com.example.hostelmanagement.dto;

import java.math.BigDecimal;


public record PendingAmountResponse(
    BigDecimal pendingAmount
) {}
