package com.example.hostelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record ChangePasswordRequest(
    @NotBlank(message = "Old password must not be blank")
    String oldPassword,

    @NotBlank(message = "New password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String newPassword
) {}
