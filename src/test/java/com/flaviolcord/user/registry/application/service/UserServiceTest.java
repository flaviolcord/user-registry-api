package com.flaviolcord.user.registry.application.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.flaviolcord.user.registry.application.usecase.FindUserByIdUseCase;
import com.flaviolcord.user.registry.application.usecase.RegisterUserUseCase;
import com.flaviolcord.user.registry.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private RegisterUserUseCase registerUserUseCase;

    @Mock
    private FindUserByIdUseCase findUserByIdUseCase;

    @InjectMocks
    private UserService userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User(1L, "john_doe", LocalDate.of(1990, 1, 1), "France", "1234567890", "Male");
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange: Mock registerUserUseCase to return the validUser
        when(registerUserUseCase.execute(validUser)).thenReturn(validUser);

        // Act
        User result = userService.registerUser(validUser);

        // Assert
        assertNotNull(result);
        assertEquals(validUser, result);
        verify(registerUserUseCase, times(1)).execute(validUser); // Verify that execute was called once on registerUserUseCase
    }

    @Test
    void shouldFindUserByIdSuccessfully() {
        // Arrange: Mock findUserByIdUseCase to return the validUser
        Long userId = 1L;
        when(findUserByIdUseCase.execute(userId)).thenReturn(validUser);

        // Act
        User result = userService.findUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(validUser, result);
        verify(findUserByIdUseCase, times(1)).execute(userId);
    }
}