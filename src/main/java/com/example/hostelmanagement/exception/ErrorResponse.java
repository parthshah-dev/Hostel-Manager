package com.example.hostelmanagement.exception;

import java.time.LocalDateTime;

/**
 * Standard error response structure returned for API exceptions.
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String message,
    String path
) {}
