package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.*;

/**
 * Service interface defining authentication business processes.
 */
public interface AuthService {

    /**
     * Registers a new ADMIN user in an inactive status and dispatches activation email.
     */
    ApiResponse signup(SignupRequest request);

    /**
     * Activates an account using the verification token.
     */
    ApiResponse activate(String token);

    /**
     * Authenticates credentials, validates active status, and issues JWT tokens.
     */
    LoginResponse login(LoginRequest request);

    /**
     * Generates password reset tokens and dispatches reset instructions.
     */
    ApiResponse forgotPassword(ForgotPasswordRequest request);

    /**
     * Resets user password using reset token verification.
     */
    ApiResponse resetPassword(ResetPasswordRequest request);

    /**
     * Changes the current password for an authenticated user.
     */
    ApiResponse changePassword(String email, ChangePasswordRequest request);
}
