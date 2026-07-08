package com.example.hostelmanagement.exception;

/**
 * Exception thrown when a complaint status transition is invalid or rules are violated.
 */
public class InvalidComplaintStatusException extends RuntimeException {
    public InvalidComplaintStatusException(String message) {
        super(message);
    }
}
