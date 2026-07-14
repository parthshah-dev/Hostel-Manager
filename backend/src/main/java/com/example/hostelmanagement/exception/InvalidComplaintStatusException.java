package com.example.hostelmanagement.exception;


public class InvalidComplaintStatusException extends RuntimeException {
    public InvalidComplaintStatusException(String message) {
        super(message);
    }
}
