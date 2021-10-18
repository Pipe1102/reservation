package com.example.backend.exception.domain;

public class UserNameExistsException extends Exception{
    public UserNameExistsException(String message) {
        super(message);
    }
}
