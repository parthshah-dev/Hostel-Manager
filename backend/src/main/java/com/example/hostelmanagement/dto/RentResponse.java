package com.example.hostelmanagement.dto;

import com.example.hostelmanagement.entity.PaymentMode;
import com.example.hostelmanagement.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO record representing a Rent response payload.
 */
public record RentResponse(
    Long id,
    Long tenantId,
    String tenantName,
    String roomNumber,
    String rentMonth,
    BigDecimal monthlyRent,
    BigDecimal amountPaid,
    BigDecimal dueAmount,
    PaymentStatus paymentStatus,
    LocalDate paymentDate,
    PaymentMode paymentMode,
    String remarks,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
