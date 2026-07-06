package com.example.hostelmanagement.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO record for adding or updating a Tenant.
 */
public record TenantRequest(
    @NotBlank(message = "Full name must not be blank")
    @Size(max = 100, message = "Full name must be at most 100 characters long")
    String fullName,

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    String email,

    @NotBlank(message = "Phone number must not be blank")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    String phoneNumber,

    @NotBlank(message = "Aadhaar number must not be blank")
    @Pattern(regexp = "^\\d{12}$", message = "Aadhaar number must be exactly 12 digits")
    String aadhaarNumber,

    @NotNull(message = "Check-in date must not be null")
    LocalDate checkInDate,

    @NotNull(message = "Security deposit must not be null")
    @DecimalMin(value = "0.00", message = "Security deposit must be greater than or equal to zero")
    BigDecimal securityDeposit,

    @NotNull(message = "Room ID must not be null")
    Long roomId
) {}
