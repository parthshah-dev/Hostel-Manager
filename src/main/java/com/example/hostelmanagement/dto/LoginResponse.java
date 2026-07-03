package com.example.hostelmanagement.dto;

/**
 * DTO for returning login results (JWT, email, role).
 */
public record LoginResponse(
    String token,
    String email,
    String role
) {}
