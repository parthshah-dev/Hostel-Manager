package com.example.hostelmanagement.service.impl;

import com.example.hostelmanagement.dto.*;
import com.example.hostelmanagement.entity.Role;
import com.example.hostelmanagement.entity.User;
import com.example.hostelmanagement.exception.*;
import com.example.hostelmanagement.repository.UserRepository;
import com.example.hostelmanagement.security.CustomUserDetailsService;
import com.example.hostelmanagement.security.JwtService;
import com.example.hostelmanagement.service.AuthService;
import com.example.hostelmanagement.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final EmailService emailService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public ApiResponse signup(SignupRequest request) {
        log.info("Processing signup request for email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email is already registered: " + request.email());
        }

        String activationToken = UUID.randomUUID().toString();
        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ADMIN)
                .enabled(false)
                .activationToken(activationToken)
                .activationTokenExpiry(LocalDateTime.now().plusHours(24))
                .build();

        userRepository.save(user);
        log.info("Saved user account (disabled). Dispatching activation email to: {}", request.email());

        emailService.sendActivationEmail(user.getEmail(), user.getFullName(), activationToken);

        return new ApiResponse("Registration successful. Please check your email to activate your account.");
    }

    @Override
    @Transactional
    public ApiResponse activate(String token) {
        log.info("Processing account activation for token: {}", token);

        User user = userRepository.findByActivationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Activation token not found or invalid"));

        if (user.isEnabled()) {
            return new ApiResponse("Account already activated.");
        }

        if (user.getActivationTokenExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Activation token expired for email: {}", user.getEmail());
            throw new ActivationTokenExpiredException("Activation token has expired");
        }

        user.setEnabled(true);
        user.setActivationToken(null);
        user.setActivationTokenExpiry(null);
        userRepository.save(user);

        log.info("Successfully activated user account: {}", user.getEmail());
        return new ApiResponse("Account activated successfully.");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Processing login request for email: {}", request.email());


        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!user.isEnabled()) {
            log.warn("Login block: Account not activated for email: {}", request.email());
            throw new EmailNotActivatedException("Account is not activated. Please check your email to activate your account.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed: Bad credentials for email: {}", request.email());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        log.info("Successfully authenticated user: {}", request.email());
        return new LoginResponse(jwtToken, user.getEmail(), user.getRole().name());
    }

    @Override
    @Transactional
    public ApiResponse forgotPassword(ForgotPasswordRequest request) {
        log.info("Processing forgot password request for email: {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.email()));

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        log.info("Saved password reset token. Dispatching reset email to: {}", request.email());
        emailService.sendForgotPasswordEmail(user.getEmail(), user.getFullName(), resetToken);

        return new ApiResponse("Password reset email sent successfully.");
    }

    @Override
    @Transactional
    public ApiResponse resetPassword(ResetPasswordRequest request) {
        log.info("Processing password reset with token");

        User user = userRepository.findByResetToken(request.token())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired password reset token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Password reset block: Token expired for email: {}", user.getEmail());
            throw new ResetTokenExpiredException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        log.info("Successfully reset password for email: {}", user.getEmail());
        return new ApiResponse("Password reset successfully.");
    }

    @Override
    @Transactional
    public ApiResponse changePassword(String email, ChangePasswordRequest request) {
        log.info("Processing password change for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            log.warn("Password change failed: Incorrect old password for user: {}", email);
            throw new InvalidCredentialsException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("Successfully changed password for user: {}", email);
        return new ApiResponse("Password changed successfully.");
    }
}
