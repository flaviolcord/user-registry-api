package com.flaviolcord.user.registry.application.usecase;

import com.flaviolcord.user.registry.application.validator.UserValidator;
import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.domain.repository.UserRepository;
import com.flaviolcord.user.registry.infrastructure.exception.UserRegistrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User(
                1L,
                "validUsername",
                LocalDate.of(2000, 1, 1),
                "France",
                "1234567890",
                "Male"
        );
    }

    @Test
    void execute_shouldValidateUser() {
        // Act
        registerUserUseCase.execute(validUser);

        // Assert
        verify(userValidator).validate(validUser);
    }

    @Test
    void execute_shouldThrowExceptionIfUsernameAlreadyTaken() {
        // Arrange
        when(userRepository.findByUsername(validUser.username()))
                .thenReturn(Optional.of(validUser));

        // Act & Assert
        assertThrows(UserRegistrationException.class, () -> registerUserUseCase.execute(validUser));

        verify(userRepository).findByUsername(validUser.username());
        verify(userValidator).validate(validUser);
        verify(userRepository, never()).save(any());
    }

    @Test
    void execute_shouldSaveUserIfValidAndUsernameNotTaken() {
        // Arrange
        when(userRepository.findByUsername(validUser.username()))
                .thenReturn(Optional.empty());
        when(userRepository.save(validUser)).thenReturn(validUser);

        // Act
        User savedUser = registerUserUseCase.execute(validUser);

        // Assert
        assertEquals(validUser, savedUser);
        verify(userValidator).validate(validUser);
        verify(userRepository).findByUsername(validUser.username());
        verify(userRepository).save(validUser);
    }
}