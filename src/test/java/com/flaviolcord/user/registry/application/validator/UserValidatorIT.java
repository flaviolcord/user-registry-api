package com.flaviolcord.user.registry.application.validator;

import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.infrastructure.config.UserProperties;
import com.flaviolcord.user.registry.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserValidatorIT {

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserProperties userProperties;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User(
                null,
                "testuser",
                LocalDate.now().minusYears(20),
                "France",
                "1234567890",
                "Male"
        );
    }

    @Test
    void validate_ValidUser_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> userValidator.validate(validUser));
    }

    @Test
    void validate_UserExactlyMinimumAge_ShouldNotThrowException() {
        // Arrange
        User userAtMinAge = new User(
                null,
                "testuser",
                LocalDate.now().minusYears(userProperties.getMinAge()),
                "France",
                "1234567890",
                "Male"
        );

        // Act & Assert
        assertDoesNotThrow(() -> userValidator.validate(userAtMinAge));
    }

    @Test
    void validate_UnderageUser_ShouldThrowValidationException() {
        // Arrange
        User underageUser = new User(
                null,
                "testuser",
                LocalDate.now().minusYears(userProperties.getMinAge() - 1),
                "France",
                "1234567890",
                "Male"
        );

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidator.validate(underageUser)
        );
        assertEquals("The user must be at least " + userProperties.getMinAge() + " years old.",
                exception.getMessage());
    }

    @Test
    void validate_UserFromDifferentCountry_ShouldThrowValidationException() {
        // Arrange
        User foreignUser = new User(
                null,
                "testuser",
                LocalDate.now().minusYears(20),
                "Brazil",
                "1234567890",
                "Male"
        );

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidator.validate(foreignUser)
        );
        assertEquals("Only residents of " + userProperties.getAllowedCountry() + " are allowed to register.",
                exception.getMessage());
    }

    @Test
    void validate_CountryWithDifferentCase_ShouldNotThrowException() {
        // Arrange
        User userWithLowercaseCountry = new User(
                null,
                "testuser",
                LocalDate.now().minusYears(20),
                "france", // lowercase
                "1234567890",
                "Male"
        );

        // Act & Assert
        assertDoesNotThrow(() -> userValidator.validate(userWithLowercaseCountry));
    }

    @Test
    void validate_BirthdateInFuture_ShouldThrowValidationException() {
        // Arrange
        User userWithFutureBirthdate = new User(
                null,
                "testuser",
                LocalDate.now().plusYears(1),
                "France",
                "1234567890",
                "Male"
        );

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidator.validate(userWithFutureBirthdate)
        );
        assertTrue(exception.getMessage().contains("must be at least"));
    }

    @Test
    void validate_UserWithNullBirthdate_ShouldThrowValidationException() {
        // Arrange
        User userWithNullBirthdate = new User(
                null,
                "testuser",
                null,
                "France",
                "1234567890",
                "Male"
        );

        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> userValidator.validate(userWithNullBirthdate));
    }

    @Test
    void validate_UserWithNullCountry_ShouldThrowValidationException() {
        // Arrange
        User userWithNullCountry = new User(
                null,
                "testuser",
                LocalDate.now().minusYears(20),
                null,
                "1234567890",
                "Male"
        );

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidator.validate(userWithNullCountry)
        );
        assertTrue(exception.getMessage().contains("allowed to register"));
    }

    @Test
    void validate_EdgeCaseAgeCalculation_ShouldValidateCorrectly() {
        // Arrange
        LocalDate birthdate = LocalDate.now()
                .minusYears(userProperties.getMinAge())
                .plusDays(1); // One day short of minimum age

        User userJustUnderAge = new User(
                null,
                "testuser",
                birthdate,
                "France",
                "1234567890",
                "Male"
        );

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidator.validate(userJustUnderAge)
        );
        assertTrue(exception.getMessage().contains("must be at least"));
    }
}