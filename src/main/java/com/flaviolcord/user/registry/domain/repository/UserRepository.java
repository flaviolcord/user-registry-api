package com.flaviolcord.user.registry.domain.repository;

import com.flaviolcord.user.registry.domain.model.User;

import java.util.Optional;

/**
 * Domain repository interface for User operations.
 * Defines the contract for user persistence operations.
 */
public interface UserRepository {

    /**
     * Saves a user to the persistence store.
     *
     * @param user the user to save
     * @return the saved user with generated ID
     */
    User save(User user);

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to find
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findById(Long id);

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByUsername(String username);
}