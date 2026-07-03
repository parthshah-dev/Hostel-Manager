package com.example.hostelmanagement.exception;

/**
 * Exception thrown when a registration attempt is made with an email that is already registered.
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
