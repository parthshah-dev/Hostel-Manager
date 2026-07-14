package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.*;


public interface AuthService {


    ApiResponse signup(SignupRequest request);


    ApiResponse activate(String token);


    LoginResponse login(LoginRequest request);


    ApiResponse forgotPassword(ForgotPasswordRequest request);


    ApiResponse resetPassword(ResetPasswordRequest request);


    ApiResponse changePassword(String email, ChangePasswordRequest request);
}
