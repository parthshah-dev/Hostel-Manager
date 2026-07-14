package com.example.hostelmanagement.dto;

import com.example.hostelmanagement.entity.RoomStatus;
import com.example.hostelmanagement.entity.RoomType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;


public record RoomRequest(
    @NotBlank(message = "Room number must not be blank")
    @Size(max = 20, message = "Room number must be at most 20 characters long")
    String roomNumber,

    @NotNull(message = "Room type must not be null")
    RoomType roomType,

    @NotNull(message = "Capacity must not be null")
    @Min(value = 1, message = "Capacity must be at least 1")
    Integer capacity,

    @NotNull(message = "Monthly rent must not be null")
    @DecimalMin(value = "0.01", message = "Monthly rent must be greater than 0")
    BigDecimal monthlyRent,

    @NotNull(message = "Room status must not be null")
    RoomStatus roomStatus
) {}
