package com.flaviolcord.user.registry.api.controller;

import com.flaviolcord.user.registry.api.dto.UserDTO;
import com.flaviolcord.user.registry.api.mapper.UserMapper;
import com.flaviolcord.user.registry.api.model.User;
import com.flaviolcord.user.registry.api.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;
    private UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid UserDTO userDTO) {
        User registeredUser = userService.registerUser(userDTO);
        UserDTO responseDTO = userMapper.toDTO(registeredUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }
}