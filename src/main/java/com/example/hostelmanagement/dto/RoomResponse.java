package com.example.hostelmanagement.dto;

import com.example.hostelmanagement.entity.RoomStatus;
import com.example.hostelmanagement.entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO record representing a Room response payload.
 */
public record RoomResponse(
    Long id,
    String roomNumber,
    RoomType roomType,
    Integer capacity,
    BigDecimal monthlyRent,
    RoomStatus roomStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
