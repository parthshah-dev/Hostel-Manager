package com.example.hostelmanagement.service;


public interface EmailService {


    void sendActivationEmail(String toEmail, String fullName, String token);


    void sendForgotPasswordEmail(String toEmail, String fullName, String token);


    void sendRentDueReminderEmail(String toEmail, String fullName, String rentMonth, java.math.BigDecimal dueAmount);


    void sendPaymentConfirmationEmail(String toEmail, String fullName, String rentMonth, java.math.BigDecimal amountPaid, String paymentMode, java.time.LocalDate paymentDate);
}
