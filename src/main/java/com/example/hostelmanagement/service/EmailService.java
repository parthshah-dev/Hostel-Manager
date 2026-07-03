package com.example.hostelmanagement.service;

/**
 * Service interface for handling email dispatches.
 */
public interface EmailService {

    /**
     * Sends the account activation email.
     *
     * @param toEmail  Recipient's email.
     * @param fullName Recipient's full name.
     * @param token    The UUID activation token.
     */
    void sendActivationEmail(String toEmail, String fullName, String token);

    /**
     * Sends the password reset email.
     *
     * @param toEmail  Recipient's email.
     * @param fullName Recipient's full name.
     * @param token    The UUID reset token.
     */
    void sendForgotPasswordEmail(String toEmail, String fullName, String token);
}
