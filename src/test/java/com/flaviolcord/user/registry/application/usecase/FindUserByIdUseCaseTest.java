package com.flaviolcord.user.registry.application.usecase;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.domain.repository.UserRepository;
import com.flaviolcord.user.registry.infrastructure.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class FindUserByIdUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FindUserByIdUseCase findUserByIdUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User(1L, "john_doe", LocalDate.of(1990, 1, 1), "France", "1234567890", "Male");
    }

    @Test
    void shouldReturnUserWhenFound() {
        // Arrange: Mock repository to return the user
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser));

        // Act
        User result = findUserByIdUseCase.execute(userId);

        // Assert
        assertNotNull(result);
        assertEquals(validUser, result);
        verify(userRepository, times(1)).findById(userId); // Verify that findById was called once
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        // Arrange: Mock repository to return empty
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            findUserByIdUseCase.execute(userId); // This should throw an exception
        });

        assertEquals("User not found with ID: 1", exception.getMessage()); // Verify exception message
        verify(userRepository, times(1)).findById(userId); // Verify that findById was called once
    }
}