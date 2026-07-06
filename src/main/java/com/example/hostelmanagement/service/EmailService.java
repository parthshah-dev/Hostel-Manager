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

    /**
     * Sends the rent due reminder email to the tenant.
     *
     * @param toEmail   Recipient's email.
     * @param fullName  Recipient's full name.
     * @param rentMonth The month of the rent.
     * @param dueAmount The remaining due amount.
     */
    void sendRentDueReminderEmail(String toEmail, String fullName, String rentMonth, java.math.BigDecimal dueAmount);

    /**
     * Sends the rent payment confirmation email to the tenant.
     *
     * @param toEmail     Recipient's email.
     * @param fullName    Recipient's full name.
     * @param rentMonth   The month of the rent.
     * @param amountPaid  The amount paid in this transaction.
     * @param paymentMode The mode of payment.
     * @param paymentDate The date of payment.
     */
    void sendPaymentConfirmationEmail(String toEmail, String fullName, String rentMonth, java.math.BigDecimal amountPaid, String paymentMode, java.time.LocalDate paymentDate);
}
