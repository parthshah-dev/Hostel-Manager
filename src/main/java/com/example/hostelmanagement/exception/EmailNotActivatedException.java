package com.example.hostelmanagement.exception;

/**
 * Exception thrown when authentication is attempted on an unactivated account.
 */
public class EmailNotActivatedException extends RuntimeException {
    public EmailNotActivatedException(String message) {
        super(message);
    }
}
