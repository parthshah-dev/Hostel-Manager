package com.example.hostelmanagement.exception;

/**
 * Exception thrown when a tenant registration attempt conflicts with an existing unique property (e.g. Email or Aadhaar).
 */
public class TenantAlreadyExistsException extends RuntimeException {
    public TenantAlreadyExistsException(String message) {
        super(message);
    }
}
