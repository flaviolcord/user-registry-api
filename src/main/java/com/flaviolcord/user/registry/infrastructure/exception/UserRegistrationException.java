package com.flaviolcord.user.registry.infrastructure.exception;

/**
 * Exception thrown when a user cannot be registered due to a conflict or issue.
 *
 * <p>This exception is specifically used in the to indicate that the username
 * is already taken during the registration process.
 *
 * <p>It signals that the operation cannot proceed because of a violation of unique constraints
 * (e.g., duplicate username) in the application domain.
 *
 * @see RuntimeException
 */
public class UserRegistrationException extends RuntimeException {

    /**
     * Constructs a new {@code UserRegistrationException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     *                (e.g., "Username is already taken")
     */
    public UserRegistrationException(String message) {
        super(message);
    }
}