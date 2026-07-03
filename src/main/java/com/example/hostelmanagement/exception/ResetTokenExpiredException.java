package com.example.hostelmanagement.exception;

/**
 * Exception thrown when the password reset token has expired.
 */
public class ResetTokenExpiredException extends RuntimeException {
    public ResetTokenExpiredException(String message) {
        super(message);
    }
}
