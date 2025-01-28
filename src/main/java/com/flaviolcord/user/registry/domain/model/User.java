package com.flaviolcord.user.registry.domain.model;

import java.time.LocalDate;

/**
 * Domain model representing a user in the system.
 * Implements an immutable record pattern for user data.
 *
 * @param id the unique identifier of the user
 * @param username the unique username of the user
 * @param birthdate the user's date of birth
 * @param countryOfResidence the user's country of residence
 * @param phoneNumber the user's phone number (optional)
 * @param gender the user's gender (optional, can be "Male", "Female", or "Other")
 */
public record User(
        Long id,
        String username,
        LocalDate birthdate,
        String countryOfResidence,
        String phoneNumber,
        String gender
){}