package com.klokin.backend.exception;

// Thrown when something the caller asked for doesn't exist (e.g. employee 99).
// The GlobalExceptionHandler turns it into an HTTP 404.
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
