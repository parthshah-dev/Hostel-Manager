package com.example.hostelmanagement.exception;

/**
 * Exception thrown when trying to create a room with a room number that already exists.
 */
public class RoomAlreadyExistsException extends RuntimeException {
    public RoomAlreadyExistsException(String message) {
        super(message);
    }
}
