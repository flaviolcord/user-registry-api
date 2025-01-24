package com.flaviolcord.user.registry.api.service;

import com.flaviolcord.user.registry.api.dto.UserDTO;
import com.flaviolcord.user.registry.api.exception.UserNotFoundException;
import com.flaviolcord.user.registry.api.exception.UserRegistrationException;
import com.flaviolcord.user.registry.api.exception.ValidationException;
import com.flaviolcord.user.registry.api.mapper.UserMapper;
import com.flaviolcord.user.registry.api.model.User;
import com.flaviolcord.user.registry.api.repository.UserRepository;
import com.flaviolcord.user.registry.api.validator.UserValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    @Value("${user.allowed.country}")
    private String allowedCountry;

    @Value("${user.min.age}")
    private int minAge;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
    }

    public User registerUser(UserDTO userDTO) {
        userValidator.validate(userDTO, allowedCountry, minAge);

        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new ValidationException("Username already exists");
        }

        User user = userMapper.toEntity(userDTO);

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UserRegistrationException("Failed to create user due to data integrity issue", ex);
        } catch (Exception ex) {
            throw new UserRegistrationException("An unexpected error occurred while registering user", ex);
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    return new UserNotFoundException("User not found with ID: " + id);
                });
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    return new UserNotFoundException("User not found with username: " + username);
                });
    }
}
