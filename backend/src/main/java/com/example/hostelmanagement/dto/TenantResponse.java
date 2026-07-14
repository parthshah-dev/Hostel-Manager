package com.example.hostelmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


public record TenantResponse(
    Long id,
    String fullName,
    String email,
    String phoneNumber,
    String aadhaarNumber,
    LocalDate checkInDate,
    BigDecimal securityDeposit,
    Long roomId,
    String roomNumber,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
