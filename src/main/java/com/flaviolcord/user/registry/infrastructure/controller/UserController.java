package com.flaviolcord.user.registry.infrastructure.controller;

import com.flaviolcord.user.registry.application.service.UserService;
import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.infrastructure.dto.UserDTO;
import com.flaviolcord.user.registry.infrastructure.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid UserDTO userDTO) {
        // Convert DTO to domain model
        User user = userMapper.toDomainModel(userDTO);

        User registeredUser = userService.registerUser(user);

        // Convert domain model back to DTO
        UserDTO responseDTO = userMapper.toDTO(registeredUser);

        // Return the response with HTTP status 201 (CREATED)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        // Retrieve the user by ID
        User user = userService.findUserById(id);

        // Convert domain model to DTO
        UserDTO responseDTO = userMapper.toDTO(user);

        // Return the response with HTTP status 200 (OK)
        return ResponseEntity.ok(responseDTO);
    }
}