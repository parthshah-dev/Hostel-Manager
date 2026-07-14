package com.example.hostelmanagement.exception;

import java.time.LocalDateTime;


public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String message,
    String path
) {}
