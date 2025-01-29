package com.flaviolcord.user.registry.infrastructure.exception;

/**
 * Exception thrown when a requested user cannot be found.
 *
 * <p>This exception is typically used in cases where an operation
 * attempts to retrieve a user by an identifier (e.g., ID or username),
 * but no corresponding user exists in the system.
 *
 * @see RuntimeException
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code UserNotFoundException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}