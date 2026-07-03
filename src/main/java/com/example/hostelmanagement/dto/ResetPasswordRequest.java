package com.example.hostelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for reset password request.
 */
public record ResetPasswordRequest(
    @NotBlank(message = "Token must not be blank")
    String token,

    @NotBlank(message = "New password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String newPassword
) {}
