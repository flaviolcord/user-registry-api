package com.flaviolcord.user.registry.application.service;

import com.flaviolcord.user.registry.application.usecase.FindUserByIdUseCase;
import com.flaviolcord.user.registry.application.usecase.RegisterUserUseCase;
import com.flaviolcord.user.registry.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for user management operations.
 * Delegates business logic to specific use cases.
 */
@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final RegisterUserUseCase registerUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;

    /**
     * Registers a new user.
     *
     * @param user the user to register
     * @return the registered user with generated ID
     * @throws com.flaviolcord.user.registry.infrastructure.exception.ValidationException if user data is invalid
     * @throws com.flaviolcord.user.registry.infrastructure.exception.UserRegistrationException if username is already taken
     */
    public User registerUser(User user) {
        return registerUserUseCase.execute(user);
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to find
     * @return the found user
     * @throws com.flaviolcord.user.registry.infrastructure.exception.UserNotFoundException if user is not found
     */
    public User findUserById(Long id) {
        return findUserByIdUseCase.execute(id);
    }
}