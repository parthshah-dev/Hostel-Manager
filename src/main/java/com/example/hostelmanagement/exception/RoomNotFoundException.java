package com.example.hostelmanagement.exception;

/**
 * Exception thrown when a requested room is not found in the system.
 */
public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String message) {
        super(message);
    }
}
