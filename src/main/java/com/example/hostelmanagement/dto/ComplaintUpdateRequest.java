package com.example.hostelmanagement.dto;

import com.example.hostelmanagement.entity.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO record for updating a Complaint's status and remarks.
 */
public record ComplaintUpdateRequest(
    @NotNull(message = "Status must not be null")
    ComplaintStatus status,

    @Size(max = 500, message = "Resolution remarks must be at most 500 characters long")
    String resolutionRemarks
) {}
