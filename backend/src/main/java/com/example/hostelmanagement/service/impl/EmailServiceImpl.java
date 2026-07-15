package com.example.hostelmanagement.service.impl;

import com.example.hostelmanagement.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;


@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final RestClient restClient;

    @Value("${brevo.api.url}")
    private String apiUrl;

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.name}")
    private String senderName;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${app.backend.url:https://hostelease-db.onrender.com}")
    private String backendUrl;

    public EmailServiceImpl() {
        this.restClient = RestClient.create();
    }

    @Override
    public void sendActivationEmail(String toEmail, String fullName, String token) {
        String subject = "Activate your Hostel Management Account";
        String activationUrl = frontendUrl + "/activate?token=" + token;

        String htmlBody = String.format(
                "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #ffffff;\">" +
                "    <div style=\"text-align: center; margin-bottom: 20px;\">" +
                "        <h2 style=\"color: #4F46E5; margin: 0;\">Hostel Management System</h2>" +
                "    </div>" +
                "    <div style=\"color: #333333; line-height: 1.6; font-size: 16px;\">" +
                "        <p>Hello <strong>%s</strong>,</p>" +
                "        <p>Thank you for registering! Please activate your account by clicking the button below:</p>" +
                "        <div style=\"text-align: center; margin: 30px 0;\">" +
                "            <a href=\"%s\" style=\"background-color: #4F46E5; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;\">Activate Account</a>" +
                "        </div>" +
                "        <p style=\"font-size: 14px; color: #666666;\">This link will expire in 24 hours.</p>" +
                "        <p style=\"font-size: 14px; color: #999999; margin-top: 20px;\">If you did not create this account, you can safely ignore this email.</p>" +
                "    </div>" +
                "    <hr style=\"border: 0; border-top: 1px solid #eeeeee; margin: 20px 0;\" />" +
                "    <div style=\"text-align: center; font-size: 12px; color: #999999;\">" +
                "        &copy; 2026 Hostel Management. All rights reserved." +
                "    </div>" +
                "</div>",
                fullName, activationUrl
        );

        String textBody = String.format(
                "Hello %s,\n\n" +
                "Thank you for registering.\n\n" +
                "Please copy and paste the following link into your browser to activate your account:\n" +
                "%s\n\n" +
                "The link expires in 24 hours.\n\n" +
                "If you did not create this account, ignore this email.",
                fullName, activationUrl
        );

        sendEmail(toEmail, fullName, subject, htmlBody, textBody);
    }

    @Override
    public void sendForgotPasswordEmail(String toEmail, String fullName, String token) {
        String subject = "Reset Password - Hostel Management";
        String resetUrl = backendUrl + "/api/auth/reset-password?token=" + token;

        String htmlBody = String.format(
                "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #ffffff;\">" +
                "    <div style=\"text-align: center; margin-bottom: 20px;\">" +
                "        <h2 style=\"color: #EF4444; margin: 0;\">Password Reset Request</h2>" +
                "    </div>" +
                "    <div style=\"color: #333333; line-height: 1.6; font-size: 16px;\">" +
                "        <p>Hello <strong>%s</strong>,</p>" +
                "        <p>We received a request to reset your password. Click the button below to set a new password:</p>" +
                "        <div style=\"text-align: center; margin: 30px 0;\">" +
                "            <a href=\"%s\" style=\"background-color: #EF4444; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;\">Reset Password</a>" +
                "        </div>" +
                "        <p style=\"font-size: 14px; color: #666666;\">This link will expire in 30 minutes.</p>" +
                "        <p style=\"font-size: 14px; color: #999999; margin-top: 20px;\">If you did not request this password reset, please ignore this email.</p>" +
                "    </div>" +
                "    <hr style=\"border: 0; border-top: 1px solid #eeeeee; margin: 20px 0;\" />" +
                "    <div style=\"text-align: center; font-size: 12px; color: #999999;\">" +
                "        &copy; 2026 Hostel Management. All rights reserved." +
                "    </div>" +
                "</div>",
                fullName, resetUrl
        );

        String textBody = String.format(
                "Hello %s,\n\n" +
                "We received a request to reset your password.\n\n" +
                "Please copy and paste the following link into your browser to reset your password:\n" +
                "%s\n\n" +
                "The link expires in 30 minutes.\n\n" +
                "If you did not request this password reset, please ignore this email.",
                fullName, resetUrl
        );

        sendEmail(toEmail, fullName, subject, htmlBody, textBody);
    }

    @Override
    public void sendRentDueReminderEmail(String toEmail, String fullName, String rentMonth, java.math.BigDecimal dueAmount) {
        String subject = "Rent Due Reminder - " + rentMonth;

        String htmlBody = String.format(
                "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #ffffff;\">" +
                "    <div style=\"text-align: center; margin-bottom: 20px;\">" +
                "        <h2 style=\"color: #4F46E5; margin: 0;\">Hostel Rent Reminder</h2>" +
                "    </div>" +
                "    <div style=\"color: #333333; line-height: 1.6; font-size: 16px;\">" +
                "        <p>Hello <strong>%s</strong>,</p>" +
                "        <p>This is a friendly reminder that your rent for the month of <strong>%s</strong> is due.</p>" +
                "        <div style=\"background-color: #F3F4F6; padding: 15px; border-radius: 6px; margin: 20px 0; border-left: 4px solid #4F46E5;\">" +
                "            <p style=\"margin: 0; font-size: 15px; color: #4B5563;\">Rent Month: <strong>%s</strong></p>" +
                "            <p style=\"margin: 5px 0 0 0; font-size: 18px; color: #111827;\">Outstanding Due Amount: <strong>INR %s</strong></p>" +
                "        </div>" +
                "        <p>Please complete your payment as soon as possible to avoid any late fees or booking conflicts.</p>" +
                "        <p style=\"font-size: 14px; color: #666666;\">If you have already paid, please ignore this reminder or contact the administrator to verify your payment status.</p>" +
                "    </div>" +
                "    <hr style=\"border: 0; border-top: 1px solid #eeeeee; margin: 20px 0;\" />" +
                "    <div style=\"text-align: center; font-size: 12px; color: #999999;\">" +
                "        &copy; 2026 Hostel Management. All rights reserved." +
                "    </div>" +
                "</div>",
                fullName, rentMonth, rentMonth, dueAmount.toString()
        );

        String textBody = String.format(
                "Hello %s,\n\n" +
                "This is a friendly reminder that your rent for the month of %s is due.\n\n" +
                "Rent Month: %s\n" +
                "Outstanding Due Amount: INR %s\n\n" +
                "Please complete your payment as soon as possible to avoid any late fees or booking conflicts.\n\n" +
                "If you have already paid, please ignore this reminder or contact the administrator to verify your payment status.",
                fullName, rentMonth, rentMonth, dueAmount.toString()
        );

        sendEmail(toEmail, fullName, subject, htmlBody, textBody);
    }

    @Override
    public void sendPaymentConfirmationEmail(String toEmail, String fullName, String rentMonth, java.math.BigDecimal amountPaid, String paymentMode, java.time.LocalDate paymentDate) {
        String subject = "Rent Payment Confirmation - " + rentMonth;

        String htmlBody = String.format(
                "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #ffffff;\">" +
                "    <div style=\"text-align: center; margin-bottom: 20px;\">" +
                "        <h2 style=\"color: #10B981; margin: 0;\">Payment Successful</h2>" +
                "    </div>" +
                "    <div style=\"color: #333333; line-height: 1.6; font-size: 16px;\">" +
                "        <p>Hello <strong>%s</strong>,</p>" +
                "        <p>We are pleased to inform you that your rent payment for <strong>%s</strong> has been successfully recorded.</p>" +
                "        <div style=\"background-color: #ECFDF5; padding: 15px; border-radius: 6px; margin: 20px 0; border-left: 4px solid #10B981;\">" +
                "            <p style=\"margin: 0; font-size: 15px; color: #047857;\">Rent Month: <strong>%s</strong></p>" +
                "            <p style=\"margin: 5px 0; font-size: 16px; color: #047857;\">Amount Paid: <strong>INR %s</strong></p>" +
                "            <p style=\"margin: 5px 0; font-size: 15px; color: #047857;\">Payment Mode: <strong>%s</strong></p>" +
                "            <p style=\"margin: 5px 0 0 0; font-size: 15px; color: #047857;\">Payment Date: <strong>%s</strong></p>" +
                "        </div>" +
                "        <p>Thank you for your prompt payment! A receipt has been generated in your account dashboard.</p>" +
                "    </div>" +
                "    <hr style=\"border: 0; border-top: 1px solid #eeeeee; margin: 20px 0;\" />" +
                "    <div style=\"text-align: center; font-size: 12px; color: #999999;\">" +
                "        &copy; 2026 Hostel Management. All rights reserved." +
                "    </div>" +
                "</div>",
                fullName, rentMonth, rentMonth, amountPaid.toString(), paymentMode, paymentDate.toString()
        );

        String textBody = String.format(
                "Hello %s,\n\n" +
                "We are pleased to inform you that your rent payment for %s has been successfully recorded.\n\n" +
                "Rent Month: %s\n" +
                "Amount Paid: INR %s\n" +
                "Payment Mode: %s\n" +
                "Payment Date: %s\n\n" +
                "Thank you for your prompt payment!",
                fullName, rentMonth, rentMonth, amountPaid.toString(), paymentMode, paymentDate.toString()
        );

        sendEmail(toEmail, fullName, subject, htmlBody, textBody);
    }

    private void sendEmail(String toEmail, String recipientName, String subject, String htmlContent, String textContent) {
        try {
            BrevoSender sender = new BrevoSender(senderName, senderEmail);
            BrevoRecipient recipient = new BrevoRecipient(toEmail, recipientName);
            BrevoEmailRequest requestPayload = new BrevoEmailRequest(sender, List.of(recipient), subject, htmlContent, textContent);

            log.info("Sending transactional email via Brevo to {}", toEmail);

            restClient.post()
                    .uri(apiUrl)
                    .header("api-key", apiKey)
                    .header("accept", "application/json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Email successfully sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {} via Brevo", toEmail, e);
        }
    }


    private record BrevoSender(String name, String email) {}
    private record BrevoRecipient(String email, String name) {}
    private record BrevoEmailRequest(
            BrevoSender sender,
            List<BrevoRecipient> to,
            String subject,
            String htmlContent,
            String textContent
    ) {}
}
