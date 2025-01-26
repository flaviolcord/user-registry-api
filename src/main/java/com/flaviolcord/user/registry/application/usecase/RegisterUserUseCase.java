package com.flaviolcord.user.registry.application.usecase;

import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.domain.repository.UserRepository;
import com.flaviolcord.user.registry.application.validator.UserValidator;
import com.flaviolcord.user.registry.infrastructure.exception.UserRegistrationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public User execute(User user) {
        // Validate the user data
        userValidator.validate(user);

        // Check if the username is already taken
        if (userRepository.findByUsername(user.username()).isPresent()) {
            throw new UserRegistrationException("Username is already taken");
        }

        // Save the user to the database
        return userRepository.save(user);
    }
}
