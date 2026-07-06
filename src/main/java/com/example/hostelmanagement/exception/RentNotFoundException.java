package com.example.hostelmanagement.exception;

/**
 * Exception thrown when a requested rent record is not found.
 */
public class RentNotFoundException extends RuntimeException {
    public RentNotFoundException(String message) {
        super(message);
    }
}
