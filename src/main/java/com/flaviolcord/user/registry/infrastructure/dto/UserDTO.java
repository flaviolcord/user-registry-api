package com.flaviolcord.user.registry.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for User operations.
 * Contains user information with validation constraints.
 */
@Getter @Setter @NoArgsConstructor
public class UserDTO {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The username of the user.
     * Must be between 3 and 50 characters long.
     */
    @NotNull(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /**
     * The birthdate of the user.
     * Must be a date in the past.
     */
    @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate must be in the past")
    private LocalDate birthdate;

    /**
     * The user's country of residence.
     * Required field.
     */
    @NotNull(message = "Country of residence is required")
    @JsonProperty("country_of_residence")
    private String countryOfResidence;

    /**
     * The user's phone number.
     * Optional field, but if provided must be between 10 and 15 characters.
     */
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    @JsonProperty("phone_number")
    private String phoneNumber; // Optional

    /**
     * The user's gender.
     * Optional field, but if provided must be either "Male", "Female", or "Other".
     */
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    private String gender; // Optional
}