package com.example.hostelmanagement.exception;

/**
 * Exception thrown when trying to assign a tenant to a room that has reached its maximum capacity.
 */
public class RoomCapacityExceededException extends RuntimeException {
    public RoomCapacityExceededException(String message) {
        super(message);
    }
}
