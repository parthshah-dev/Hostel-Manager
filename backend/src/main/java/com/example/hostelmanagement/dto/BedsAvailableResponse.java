package com.example.hostelmanagement.dto;

import java.math.BigDecimal;

public record BedsAvailableResponse(
    Long id,
    String roomNumber,
    Integer bedsAvailable,
    BigDecimal monthlyRent
) {}
