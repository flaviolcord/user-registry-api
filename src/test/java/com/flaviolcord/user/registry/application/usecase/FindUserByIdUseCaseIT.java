package com.flaviolcord.user.registry.application.usecase;

import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.domain.repository.UserRepository;
import com.flaviolcord.user.registry.infrastructure.exception.UserNotFoundException;
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
class FindUserByIdUseCaseIT {

    @Autowired
    private FindUserByIdUseCase findUserByIdUseCase;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        User user = new User(
                null,
                "testuser",
                LocalDate.now().minusYears(20),
                "France",
                "1234567890",
                "Male"
        );
        savedUser = userRepository.save(user);
    }

    @Test
    void execute_WithExistingId_ShouldReturnUser() {
        // Act
        User foundUser = findUserByIdUseCase.execute(savedUser.id());

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
    void execute_WithNonExistingId_ShouldThrowNotFoundException() {
        // Arrange
        Long nonExistentId = 999L;

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> findUserByIdUseCase.execute(nonExistentId)
        );
        assertEquals("User not found with ID: " + nonExistentId, exception.getMessage());
    }
}
