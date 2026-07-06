package com.example.hostelmanagement.exception;

/**
 * Exception thrown when a tenant lookup fails.
 */
public class TenantNotFoundException extends RuntimeException {
    public TenantNotFoundException(String message) {
        super(message);
    }
}
