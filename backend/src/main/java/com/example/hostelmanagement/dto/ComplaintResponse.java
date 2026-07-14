package com.example.hostelmanagement.dto;

import com.example.hostelmanagement.entity.ComplaintCategory;
import com.example.hostelmanagement.entity.ComplaintStatus;
import java.time.LocalDateTime;


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
