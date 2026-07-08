package com.example.hostelmanagement.dto;

import com.example.hostelmanagement.entity.PaymentMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO record for completing a Rent payment.
 */
public record RentPaymentRequest(
    @NotNull(message = "Payment mode must not be null")
    PaymentMode paymentMode,

    @Size(max = 255, message = "Remarks must be at most 255 characters long")
    String remarks
) {}
