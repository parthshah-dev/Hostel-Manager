package com.example.hostelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record GenerateRentRequest(
    @NotBlank(message = "Rent month must not be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "Rent month must be in YYYY-MM format (e.g. 2026-08)")
    String rentMonth
) {}
