package com.flaviolcord.user.registry.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 *
 * <p>This class provides centralized exception handling for all controllers.
 * It captures specific exceptions and maps them to appropriate HTTP responses with detailed error messages.
 *
 * <p>The exceptions handled include:
 * <ul>
 *     <li>{@link ValidationException} - For invalid user input.</li>
 *     <li>{@link UserRegistrationException} - For user registration conflicts (e.g., username already taken).</li>
 *     <li>{@link UserNotFoundException} - For cases where the requested user is not found.</li>
 *     <li>{@link MethodArgumentNotValidException} - For validation errors on method arguments.</li>
 * </ul>
 *
 * <p>Each exception is converted into a standardized error response, containing:
 * <ul>
 *     <li>HTTP status code</li>
 *     <li>Error type</li>
 *     <li>Descriptive error message</li>
 * </ul>
 *
 * @see com.flaviolcord.user.registry.infrastructure.controller.UserController
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link com.flaviolcord.user.registry.infrastructure.exception.ValidationException}.
     *
     * @param ex the exception thrown when user data is invalid
     * @return a ResponseEntity containing an error response with HTTP status 400 (BAD REQUEST)
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation error", ex.getMessage());
    }

    /**
     * Handles {@link UserRegistrationException}.
     *
     * @param ex the exception thrown when there is a conflict during user registration (e.g., username already taken)
     * @return a ResponseEntity containing an error response with HTTP status 400 (BAD REQUEST)
     */
    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleUserRegistrationException(UserRegistrationException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Registration error", ex.getMessage());
    }

    /**
     * Handles {@link UserNotFoundException}.
     *
     * @param ex the exception thrown when the requested user is not found
     * @return a ResponseEntity containing an error response with HTTP status 404 (NOT FOUND)
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found", ex.getMessage());
    }

    /**
     * Handles {@link MethodArgumentNotValidException}.
     *
     * @param ex the exception thrown for validation errors on method arguments
     * @return a ResponseEntity containing an error response with HTTP status 400 (BAD REQUEST)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation error", errorMessage);
    }

    /**
     * Builds a standardized error response.
     *
     * @param status  the HTTP status to return
     * @param error   the type of error (e.g., "Validation error")
     * @param message the detailed error message
     * @return a ResponseEntity containing the standardized error response
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), error, message);
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Represents a standardized error response for the API.
     *
     * <p>This class is used to provide structured error information in the API responses,
     * including the HTTP status code, error type, and descriptive message.
     */
    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
    }
}
