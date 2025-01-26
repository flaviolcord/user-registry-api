package com.flaviolcord.user.registry.application.service;

import com.flaviolcord.user.registry.infrastructure.config.UserProperties;
import com.flaviolcord.user.registry.infrastructure.dto.UserDTO;
import com.flaviolcord.user.registry.infrastructure.exception.UserNotFoundException;
import com.flaviolcord.user.registry.infrastructure.exception.UserRegistrationException;
import com.flaviolcord.user.registry.infrastructure.exception.ValidationException;
import com.flaviolcord.user.registry.infrastructure.mapper.UserMapper;
import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.infrastructure.repository.UserRepository;
import com.flaviolcord.user.registry.application.validator.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final UserProperties userProperties;

    public User registerUser(UserDTO userDTO) {
        userValidator.validate(userDTO, userProperties.getAllowedCountry(), userProperties.getMinAge());

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
}