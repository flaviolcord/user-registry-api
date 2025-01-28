package com.flaviolcord.user.registry.infrastructure.persistence.repository;

import com.flaviolcord.user.registry.infrastructure.persistence.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for UserEntity.
 * Provides basic CRUD operations and custom queries for user persistence.
 */
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Finds a user entity by username.
     * Implemented automatically by Spring Data JPA.
     *
     * @param username the username to search for
     * @return an Optional containing the user entity if found, or empty if not found
     */
    Optional<UserEntity> findByUsername(String username);
}
