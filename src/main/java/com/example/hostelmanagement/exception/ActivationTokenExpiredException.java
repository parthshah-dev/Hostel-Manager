package com.example.hostelmanagement.exception;

/**
 * Exception thrown when the account activation token has expired.
 */
public class ActivationTokenExpiredException extends RuntimeException {
    public ActivationTokenExpiredException(String message) {
        super(message);
    }
}
