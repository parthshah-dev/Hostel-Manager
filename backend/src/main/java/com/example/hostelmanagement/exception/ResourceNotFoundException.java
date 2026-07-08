package com.example.hostelmanagement.exception;

/**
 * Exception thrown when a requested resource (like user or token) is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
