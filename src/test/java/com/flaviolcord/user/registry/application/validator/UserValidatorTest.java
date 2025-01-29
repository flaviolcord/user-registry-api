package com.flaviolcord.user.registry.application.validator;

import static org.junit.jupiter.api.Assertions.*;

import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.infrastructure.config.UserProperties;
import com.flaviolcord.user.registry.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserProperties userProperties;

    @InjectMocks
    private UserValidator validator;

    private final String ALLOWED_COUNTRY = "France";
    private final int MIN_AGE = 18;

    @Test
    void validate_shouldPassWhenUserIsValid() {
        // Arrange
        when(userProperties.getMinAge()).thenReturn(MIN_AGE);
        when(userProperties.getAllowedCountry()).thenReturn(ALLOWED_COUNTRY);

        User validUser = new User(1L, "flavio", LocalDate.now().minusYears(20), ALLOWED_COUNTRY, "1234567890", "Male");

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(validUser));
    }

    @Test
    void validate_shouldThrowExceptionWhenUserIsTooYoung() {
        // Arrange
        when(userProperties.getMinAge()).thenReturn(MIN_AGE);

        User youngUser = new User(1L, "flavio", LocalDate.now().minusYears(15), ALLOWED_COUNTRY, "1234567890", "Male");

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> validator.validate(youngUser));
        assertEquals("The user must be at least " + MIN_AGE + " years old.", exception.getMessage());
    }

    @Test
    void validate_shouldThrowExceptionWhenUserIsFromInvalidCountry() {
        // Arrange
        when(userProperties.getAllowedCountry()).thenReturn(ALLOWED_COUNTRY);

        User invalidCountryUser = new User(1L, "flavio", LocalDate.now().minusYears(20), "Germany", "1234567890", "Male");

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> validator.validate(invalidCountryUser));
        assertEquals("Only residents of " + ALLOWED_COUNTRY + " are allowed to register.", exception.getMessage());
    }
}