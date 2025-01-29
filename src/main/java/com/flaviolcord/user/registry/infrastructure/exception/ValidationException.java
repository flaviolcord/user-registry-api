package com.flaviolcord.user.registry.infrastructure.exception;

/**
 * Exception thrown when data validation fails.
 *
 * <p>This exception is typically used to indicate that user input
 * or application data does not meet the required validation rules.
 *
 * @see RuntimeException
 */
public class ValidationException extends RuntimeException {

    /**
     * Constructs a new {@code ValidationException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public ValidationException(String message) {
        super(message);
    }
}