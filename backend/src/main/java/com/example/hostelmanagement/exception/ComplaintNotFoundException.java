package com.example.hostelmanagement.exception;

/**
 * Exception thrown when a requested complaint is not found.
 */
public class ComplaintNotFoundException extends RuntimeException {
    public ComplaintNotFoundException(String message) {
        super(message);
    }
}
