package com.example.hostelmanagement.dto;

import com.example.hostelmanagement.entity.ComplaintCategory;
import com.example.hostelmanagement.entity.ComplaintStatus;
import java.time.LocalDateTime;

/**
 * DTO record representing a Complaint response payload.
 */
public record ComplaintResponse(
    Long id,
    Long tenantId,
    String tenantName,
    String roomNumber,
    ComplaintCategory complaintCategory,
    String description,
    ComplaintStatus status,
    String resolutionRemarks,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
