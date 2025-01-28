package com.flaviolcord.user.registry.application.usecase;

import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.domain.repository.UserRepository;
import com.flaviolcord.user.registry.infrastructure.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for finding a user by their ID.
 * Implements the business logic for user retrieval.
 */
@Service
@Transactional
@AllArgsConstructor
public class FindUserByIdUseCase {

    private final UserRepository userRepository;

    /**
     * Executes the use case to find a user by ID.
     *
     * @param id the ID of the user to find
     * @return the found User
     * @throws UserNotFoundException if no user is found with the given ID
     */
    public User execute(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }
}