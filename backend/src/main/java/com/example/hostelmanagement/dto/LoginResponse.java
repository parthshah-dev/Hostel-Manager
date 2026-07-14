package com.example.hostelmanagement.dto;


public record LoginResponse(
    String token,
    String email,
    String role
) {}
