package com.example.backend.exception.domain;

public class NotAnImageFileException extends Exception {
    public NotAnImageFileException(String message) {
        super(message);
    }
}
