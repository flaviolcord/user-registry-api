package com.flaviolcord.user.registry.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.domain.repository.UserRepository;
import com.flaviolcord.user.registry.infrastructure.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserRegistrationFlowIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Clean state for each test
    }

    @Test
    void completeUserRegistrationFlow() throws Exception {
        // 1. Create User DTO
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setBirthdate(LocalDate.now().minusYears(20));
        userDTO.setCountryOfResidence("France");
        userDTO.setPhoneNumber("1234567890");
        userDTO.setGender("Male");

        // 2. Register User
        String responseJson = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 3. Extract ID from response
        Long userId = objectMapper.readTree(responseJson).get("id").asLong();

        // 4. Verify User in Database
        Optional<User> savedUser = userRepository.findById(userId);
        assertTrue(savedUser.isPresent());
        assertEquals(userDTO.getUsername(), savedUser.get().username());

        // 5. Retrieve User by ID
        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()));
    }

    @Test
    void completeUserRegistrationFlowWithValidationFailure() throws Exception {
        // 1. Create Invalid User DTO
        UserDTO invalidUserDTO = new UserDTO();
        invalidUserDTO.setUsername("testuser");
        invalidUserDTO.setBirthdate(LocalDate.now().minusYears(15)); // Under age
        invalidUserDTO.setCountryOfResidence("Brazil"); // Wrong country
        invalidUserDTO.setPhoneNumber("1234567890");
        invalidUserDTO.setGender("Male");

        // 2. Attempt Registration
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDTO)))
                .andExpect(status().isBadRequest());

        // 3. Verify User Not in Database
        assertTrue(userRepository.findByUsername(invalidUserDTO.getUsername()).isEmpty());
    }

    @Test
    void completeUserRegistrationFlowWithDuplicateUsername() throws Exception {
        // 1. Create User DTO
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setBirthdate(LocalDate.now().minusYears(20));
        userDTO.setCountryOfResidence("France");
        userDTO.setPhoneNumber("1234567890");
        userDTO.setGender("Male");

        // 2. First Registration
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        // 3. Second Registration Attempt
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());

        // 4. Verify Only One User Exists
        long count = userRepository.findByUsername(userDTO.getUsername())
                .stream()
                .count();
        assertEquals(1, count);
    }
}