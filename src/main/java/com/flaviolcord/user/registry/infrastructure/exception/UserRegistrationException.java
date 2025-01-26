package com.flaviolcord.user.registry.infrastructure.exception;

public class UserRegistrationException extends RuntimeException {

    public UserRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}