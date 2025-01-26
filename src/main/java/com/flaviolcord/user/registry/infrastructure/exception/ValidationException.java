package com.flaviolcord.user.registry.infrastructure.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}