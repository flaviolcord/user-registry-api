package com.flaviolcord.user.registry.infrastructure.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        // Arrange
        String errorMessage = "Validation failed";
        ValidationException ex = new ValidationException(errorMessage);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleValidationException(ex);

        // Assert
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertEquals(400, response.getBody().getStatus()),
                () -> assertEquals("Validation error", response.getBody().getError()),
                () -> assertEquals(errorMessage, response.getBody().getMessage())
        );
    }

    @Test
    void handleUserRegistrationException_ShouldReturnBadRequest() {
        // Arrange
        String errorMessage = "Registration failed";
        UserRegistrationException ex = new UserRegistrationException(errorMessage);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleUserRegistrationException(ex);

        // Assert
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertEquals(400, response.getBody().getStatus()),
                () -> assertEquals("Registration error", response.getBody().getError()),
                () -> assertEquals(errorMessage, response.getBody().getMessage())
        );
    }

    @Test
    void handleUserNotFoundException_ShouldReturnNotFound() {
        // Arrange
        String errorMessage = "User not found";
        UserNotFoundException ex = new UserNotFoundException(errorMessage);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleUserNotFoundException(ex);

        // Assert
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                () -> assertEquals(404, response.getBody().getStatus()),
                () -> assertEquals("User not found", response.getBody().getError()),
                () -> assertEquals(errorMessage, response.getBody().getMessage())
        );
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequest() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("object", "field1", "must not be blank");
        FieldError fieldError2 = new FieldError("object", "field2", "must be valid");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleMethodArgumentNotValidException(ex);

        // Assert
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertEquals(400, response.getBody().getStatus()),
                () -> assertEquals("Validation error", response.getBody().getError()),
                () -> assertEquals("field1: must not be blank, field2: must be valid",
                        response.getBody().getMessage())
        );
    }

    @Test
    void errorResponse_ShouldHaveCorrectGettersAndSetters() {
        // Arrange
        GlobalExceptionHandler.ErrorResponse errorResponse =
                new GlobalExceptionHandler.ErrorResponse(400, "Test Error", "Test Message");

        // Assert
        assertAll(
                () -> assertEquals(400, errorResponse.getStatus()),
                () -> assertEquals("Test Error", errorResponse.getError()),
                () -> assertEquals("Test Message", errorResponse.getMessage())
        );
    }
}