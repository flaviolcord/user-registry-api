package com.flaviolcord.user.registry.application.service;

import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.domain.repository.UserRepository;
import com.flaviolcord.user.registry.infrastructure.config.UserProperties;
import com.flaviolcord.user.registry.infrastructure.exception.UserNotFoundException;
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
class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProperties userProperties;

    private User validUser;

    @BeforeEach
    void setUp() {
        // Create a valid user for tests
        validUser = new User(
                null,
                "testuser",
                LocalDate.now().minusYears(20), // Valid age > 18
                "France",                        // Valid country
                "1234567890",
                "Male"
        );
    }

    @Test
    void registerUser_WithValidData_ShouldSaveAndReturnUser() {
        // Act
        User registeredUser = userService.registerUser(validUser);

        // Assert
        assertNotNull(registeredUser.id());
        assertEquals(validUser.username(), registeredUser.username());
        assertEquals(validUser.birthdate(), registeredUser.birthdate());
        assertEquals(validUser.countryOfResidence(), registeredUser.countryOfResidence());
        assertEquals(validUser.phoneNumber(), registeredUser.phoneNumber());
        assertEquals(validUser.gender(), registeredUser.gender());

        // Verify it's in the database
        assertTrue(userRepository.findById(registeredUser.id()).isPresent());
    }

    @Test
    void registerUser_WithDuplicateUsername_ShouldThrowException() {
        // Arrange
        userService.registerUser(validUser);

        User duplicateUser = new User(
                null,
                validUser.username(), // Same username
                LocalDate.now().minusYears(25),
                "France",
                "9876543210",
                "Female"
        );

        // Act & Assert
        UserRegistrationException exception = assertThrows(
                UserRegistrationException.class,
                () -> userService.registerUser(duplicateUser)
        );
        assertEquals("Username is already taken", exception.getMessage());
    }

    @Test
    void registerUser_WithInvalidAge_ShouldThrowException() {
        // Arrange
        User underageUser = new User(
                null,
                "younguser",
                LocalDate.now().minusYears(userProperties.getMinAge() - 1),
                "France",
                "1234567890",
                "Male"
        );

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.registerUser(underageUser)
        );
        assertTrue(exception.getMessage().contains("must be at least " + userProperties.getMinAge()));
    }

    @Test
    void registerUser_WithInvalidCountry_ShouldThrowException() {
        // Arrange
        User foreignUser = new User(
                null,
                "foreignuser",
                LocalDate.now().minusYears(20),
                "Brazil",
                "1234567890",
                "Female"
        );

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.registerUser(foreignUser)
        );
        assertTrue(exception.getMessage().contains("Only residents of " + userProperties.getAllowedCountry()));
    }

    @Test
    void findUserById_WithExistingUser_ShouldReturnUser() {
        // Arrange
        User savedUser = userService.registerUser(validUser);

        // Act
        User foundUser = userService.findUserById(savedUser.id());

        // Assert
        assertNotNull(foundUser);
        assertEquals(savedUser.id(), foundUser.id());
        assertEquals(savedUser.username(), foundUser.username());
        assertEquals(savedUser.birthdate(), foundUser.birthdate());
        assertEquals(savedUser.countryOfResidence(), foundUser.countryOfResidence());
        assertEquals(savedUser.phoneNumber(), foundUser.phoneNumber());
        assertEquals(savedUser.gender(), foundUser.gender());
    }

    @Test
    void findUserById_WithNonExistingUser_ShouldThrowException() {
        // Arrange
        Long nonExistentId = 999L;

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findUserById(nonExistentId)
        );
        assertEquals("User not found with ID: " + nonExistentId, exception.getMessage());
    }

    @Test
    void registerUser_WithOptionalFieldsNull_ShouldSaveSuccessfully() {
        // Arrange
        User userWithoutOptionals = new User(
                null,
                "minimaluser",
                LocalDate.now().minusYears(20),
                "France",
                null,    // Optional phone number
                null     // Optional gender
        );

        // Act
        User savedUser = userService.registerUser(userWithoutOptionals);

        // Assert
        assertNotNull(savedUser.id());
        assertNull(savedUser.phoneNumber());
        assertNull(savedUser.gender());

        // Verify it's in the database
        User retrievedUser = userService.findUserById(savedUser.id());
        assertNull(retrievedUser.phoneNumber());
        assertNull(retrievedUser.gender());
    }

    @Test
    void registerUser_ShouldTriggerValidation() {
        // Arrange - Create user with edge case values
        User edgeCaseUser = new User(
                null,
                "testuser",
                LocalDate.now().minusYears(userProperties.getMinAge()).plusDays(1), // Just under minimum age
                userProperties.getAllowedCountry().toLowerCase(), // Test case sensitivity
                "1234567890",
                "Male"
        );

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.registerUser(edgeCaseUser)
        );
        assertTrue(exception.getMessage().contains("must be at least"));
    }
}