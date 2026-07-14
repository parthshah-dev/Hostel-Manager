package com.example.hostelmanagement.exception;


public class RentAlreadyGeneratedException extends RuntimeException {
    public RentAlreadyGeneratedException(String message) {
        super(message);
    }
}
