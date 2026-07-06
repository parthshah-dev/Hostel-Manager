package com.example.hostelmanagement.exception;

/**
 * Exception thrown when a payment attempt is made on a rent record that has already been PAID.
 */
public class RentAlreadyPaidException extends RuntimeException {
    public RentAlreadyPaidException(String message) {
        super(message);
    }
}
