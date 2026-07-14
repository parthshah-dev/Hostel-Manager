package com.example.hostelmanagement.exception;


public class RentAlreadyPaidException extends RuntimeException {
    public RentAlreadyPaidException(String message) {
        super(message);
    }
}
