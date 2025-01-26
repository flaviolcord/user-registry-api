package com.flaviolcord.user.registry.application.validator;

import com.flaviolcord.user.registry.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserValidator {
    private final UserInputValidator userInputValidator;
    private final UserBusinessRuleValidator userBusinessRuleValidator;

    public void validate(User user) {
        userInputValidator.validate(user);
        userBusinessRuleValidator.validate(user);
    }
}