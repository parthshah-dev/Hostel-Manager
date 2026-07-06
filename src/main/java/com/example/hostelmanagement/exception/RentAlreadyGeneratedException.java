package com.example.hostelmanagement.exception;

/**
 * Exception thrown when trying to generate rent for a tenant and month combination
 * that already has a rent record in the system.
 */
public class RentAlreadyGeneratedException extends RuntimeException {
    public RentAlreadyGeneratedException(String message) {
        super(message);
    }
}
