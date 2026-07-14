package com.example.hostelmanagement.dto;

import com.example.hostelmanagement.entity.ComplaintCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record ComplaintRequest(
    @NotNull(message = "Tenant ID must not be null")
    Long tenantId,

    @NotNull(message = "Complaint category must not be null")
    ComplaintCategory complaintCategory,

    @NotBlank(message = "Description must not be blank")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters long")
    String description
) {}
