package com.flaviolcord.user.registry.api.validator;

import com.flaviolcord.user.registry.api.dto.UserDTO;
import com.flaviolcord.user.registry.api.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class UserValidator {
    public void validate(UserDTO userDTO, String allowedCountry, int minAge) {
        validateAge(userDTO, minAge);
        validateCountry(userDTO, allowedCountry);
    }

    private void validateAge(UserDTO userDTO, int minAge) {
        int userAge = Period.between(userDTO.getBirthdate(), LocalDate.now()).getYears();
        if (userAge < minAge) {
            throw new ValidationException("User must be at least " + minAge + " years old.");
        }
    }

    private void validateCountry(UserDTO userDTO, String allowedCountry) {
        if (!allowedCountry.equalsIgnoreCase(userDTO.getCountryOfResidence())) {
            throw new ValidationException("Only residents of " + allowedCountry + " are allowed to register.");
        }
    }
}