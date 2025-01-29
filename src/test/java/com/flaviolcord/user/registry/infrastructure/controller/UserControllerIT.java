package com.flaviolcord.user.registry.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaviolcord.user.registry.UserRegistryApplication;
import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.domain.repository.UserRepository;
import com.flaviolcord.user.registry.infrastructure.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = UserRegistryApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        UserDTO userDTO = createValidUserDTO();

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is(userDTO.getUsername())))
                .andExpect(jsonPath("$.country_of_residence", is("France")))
                .andExpect(jsonPath("$.phone_number", is(userDTO.getPhoneNumber())))
                .andExpect(jsonPath("$.gender", is(userDTO.getGender())));
    }

    @Test
    void registerUser_WithInvalidAge_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserDTO userDTO = createValidUserDTO();
        userDTO.setBirthdate(LocalDate.now().minusYears(17)); // Under 18

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")))
                .andExpect(jsonPath("$.message").value("The user must be at least 18 years old."));
    }

    @Test
    void registerUser_WithInvalidCountry_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserDTO userDTO = createValidUserDTO();
        userDTO.setCountryOfResidence("Brazil"); // Not France

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")))
                .andExpect(jsonPath("$.message").value("Only residents of France are allowed to register."));
    }

    @Test
    void registerUser_WithMissingRequiredFields_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Username is required")));
    }

    @Test
    void registerUser_WithDuplicateUsername_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserDTO userDTO = createValidUserDTO();

        // First registration
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        // Act & Assert - Second registration with same username
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Registration error")))
                .andExpect(jsonPath("$.message", is("Username is already taken")));
    }

    @Test
    void getUserById_ExistingUser_ShouldReturnUser() throws Exception {
        // Arrange
        User savedUser = userRepository.save(createValidUser());

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}", savedUser.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedUser.id().intValue())))
                .andExpect(jsonPath("$.username", is(savedUser.username())))
                .andExpect(jsonPath("$.country_of_residence", is(savedUser.countryOfResidence())))
                .andExpect(jsonPath("$.phone_number", is(savedUser.phoneNumber())))
                .andExpect(jsonPath("$.gender", is(savedUser.gender())));
    }

    @Test
    void getUserById_NonExistingUser_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found")))
                .andExpect(jsonPath("$.message", is("User not found with ID: 999")));
    }

    private UserDTO createValidUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setBirthdate(LocalDate.now().minusYears(20));
        userDTO.setCountryOfResidence("France");
        userDTO.setPhoneNumber("1234567890");
        userDTO.setGender("Male");
        return userDTO;
    }

    private User createValidUser() {
        return new User(
                null,
                "testuser",
                LocalDate.now().minusYears(20),
                "France",
                "1234567890",
                "Male"
        );
    }
}
