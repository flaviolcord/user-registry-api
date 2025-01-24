package com.flaviolcord.user.registry.api.service;

import com.flaviolcord.user.registry.api.exception.ValidationException;
import com.flaviolcord.user.registry.api.model.User;
import com.flaviolcord.user.registry.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Value("${user.allowed.country}")
    private String allowedCountry;

    @Value("${user.min.age}")
    private int minAge;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        validateUser(user);
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    private void validateUser(User user) {
        if (Period.between(user.getBirthdate(), LocalDate.now()).getYears() < minAge) {
            throw new ValidationException("User must be at least " + minAge + " years old.");
        }

        if (!allowedCountry.equalsIgnoreCase(user.getCountryOfResidence())) {
            throw new ValidationException("Only residents of " + allowedCountry + " are allowed to register.");
        }
    }
}