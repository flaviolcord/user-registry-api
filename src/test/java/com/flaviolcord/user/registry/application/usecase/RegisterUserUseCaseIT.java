package com.flaviolcord.user.registry.application.usecase;

import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.domain.repository.UserRepository;
import com.flaviolcord.user.registry.infrastructure.config.UserProperties;
import com.flaviolcord.user.registry.infrastructure.exception.UserRegistrationException;
import com.flaviolcord.user.registry.infrastructure.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class RegisterUserUseCaseIT {

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private UserRepository userRepository;

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
    void execute_WithValidUser_ShouldRegisterSuccessfully() {
        // Act
        User registeredUser = registerUserUseCase.execute(validUser);

        // Assert
        assertNotNull(registeredUser.id());
        assertEquals(validUser.username(), registeredUser.username());

        // Verify user was persisted
        assertTrue(userRepository.findById(registeredUser.id()).isPresent());
    }

    @Test
    void execute_WithUnderageUser_ShouldThrowValidationException() {
        // Arrange
        User underageUser = new User(
                null,
                "young_user",
                LocalDate.now().minusYears(userProperties.getMinAge() - 1),
                "France",
                "1234567890",
                "Male"
        );

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> registerUserUseCase.execute(underageUser)
        );
        assertTrue(exception.getMessage().contains("must be at least " + userProperties.getMinAge()));
    }

    @Test
    void execute_WithInvalidCountry_ShouldThrowValidationException() {
        // Arrange
        User foreignUser = new User(
                null,
                "foreign_user",
                LocalDate.now().minusYears(20),
                "Brazil",
                "1234567890",
                "Male"
        );

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> registerUserUseCase.execute(foreignUser)
        );
        assertTrue(exception.getMessage().contains("Only residents of " + userProperties.getAllowedCountry()));
    }

    @Test
    void execute_WithExistingUsername_ShouldThrowRegistrationException() {
        // Arrange
        registerUserUseCase.execute(validUser);

        User duplicateUser = new User(
                null,
                validUser.username(),
                LocalDate.now().minusYears(25),
                "France",
                "9876543210",
                "Female"
        );

        // Act & Assert
        UserRegistrationException exception = assertThrows(
                UserRegistrationException.class,
                () -> registerUserUseCase.execute(duplicateUser)
        );
        assertEquals("Username is already taken", exception.getMessage());
    }

    @Test
    void execute_WithOptionalFieldsNull_ShouldRegisterSuccessfully() {
        // Arrange
        User userWithoutOptionals = new User(
                null,
                "minimal_user",
                LocalDate.now().minusYears(20),
                "France",
                null,  // Optional phone
                null   // Optional gender
        );

        // Act
        User registeredUser = registerUserUseCase.execute(userWithoutOptionals);

        // Assert
        assertNotNull(registeredUser.id());
        assertNull(registeredUser.phoneNumber());
        assertNull(registeredUser.gender());
    }
}