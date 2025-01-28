package com.flaviolcord.user.registry.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * JPA entity representing a user in the database.
 * Maps the user data to the 'users' table.
 */
@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserEntity {

    /**
     * Auto-generated unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's unique username.
     * Cannot be null and must be unique in the database.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * User's date of birth.
     * Cannot be null.
     */
    @Column(nullable = false)
    private LocalDate birthdate;

    /**
     * User's country of residence.
     * Cannot be null and is stored in snake_case format.
     */
    @Column(name = "country_of_residence", nullable = false)
    private String countryOfResidence;

    /**
     * User's phone number.
     * Optional field stored in snake_case format.
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * User's gender.
     * Optional field that can be "Male", "Female", or "Other".
     */
    private String gender;
}
