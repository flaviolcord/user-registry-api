package com.flaviolcord.user.registry.application.validator;

import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.infrastructure.config.UserProperties;
import com.flaviolcord.user.registry.infrastructure.exception.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
@AllArgsConstructor
public class UserBusinessRuleValidator {

    private final UserProperties userProperties;

    public void validate(User user) {
        validateAge(user);
        validateCountry(user);
    }

    private void validateAge(User user) {
        int userAge = Period.between(user.birthdate(), LocalDate.now()).getYears();
        if (userAge < userProperties.getMinAge()) {
            throw new ValidationException("The user must be at least " + userProperties.getMinAge() + " years old.");
        }
    }

    private void validateCountry(User user) {
        if (!userProperties.getAllowedCountry().equalsIgnoreCase(user.countryOfResidence())) {
            throw new ValidationException("Only residents of " + userProperties.getAllowedCountry() + " are allowed to register.");
        }
    }
}
