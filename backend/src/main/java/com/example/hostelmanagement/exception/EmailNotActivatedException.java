package com.example.hostelmanagement.exception;


public class EmailNotActivatedException extends RuntimeException {
    public EmailNotActivatedException(String message) {
        super(message);
    }
}
