package com.flaviolcord.user.registry.application.validator;

import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.infrastructure.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserInputValidator {

    // Constants for repeated values
    private static final String GENDER_PATTERN = "Male|Female|Other";
    private static final String COUNTRY_OF_RESIDENCE = "France";

    public void validate(User user) {
        validateUsername(user.username());
        validateBirthdate(user.birthdate());
        validateCountryOfResidence(user.countryOfResidence());
        validatePhoneNumber(user.phoneNumber());
        validateGender(user.gender());
    }

    private void validateUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 50) {
            throw new ValidationException("The username must be between 3 and 50 characters long.");
        }
    }

    private void validateBirthdate(LocalDate birthdate) {
        if (birthdate == null || birthdate.isAfter(LocalDate.now())) {
            throw new ValidationException("The birthdate must be in the past.");
        }
    }

    private void validateCountryOfResidence(String countryOfResidence) {
        if (countryOfResidence == null || !countryOfResidence.equals(COUNTRY_OF_RESIDENCE)) {
            throw new ValidationException("Only residents of " + COUNTRY_OF_RESIDENCE + " are allowed.");
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null && (phoneNumber.length() < 10 || phoneNumber.length() > 15)) {
            throw new ValidationException("The phone number must be between 10 and 15 characters long.");
        }
    }

    private void validateGender(String gender) {
        if (gender != null && !gender.matches(GENDER_PATTERN)) {
            throw new ValidationException("The gender must be one of: Male, Female, or Other.");
        }
    }
}