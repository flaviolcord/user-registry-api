package com.flaviolcord.user.registry.infrastructure.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaviolcord.user.registry.infrastructure.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GlobalExceptionHandlerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void handleValidationException_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setBirthdate(LocalDate.now().minusYears(10)); // Will trigger age validation error
        userDTO.setCountryOfResidence("Brazil"); // Will trigger country validation error

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation error")))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void handleUserRegistrationException_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setBirthdate(LocalDate.now().minusYears(20));
        userDTO.setCountryOfResidence("France");

        // First registration
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        // Act & Assert - Second registration with same username
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Registration error")))
                .andExpect(jsonPath("$.message", is("Username is already taken")));
    }

    @Test
    void handleUserNotFoundException_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("User not found")))
                .andExpect(jsonPath("$.message", is("User not found with ID: 999")));
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserDTO invalidUser = new UserDTO(); // Missing all required fields

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation error")))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void handleMethodArgumentNotValidException_WithMultipleErrors_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserDTO invalidUser = new UserDTO();
        invalidUser.setUsername("a"); // Too short
        invalidUser.setGender("InvalidGender"); // Invalid gender value

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation error")))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void errorResponse_ShouldHaveCorrectStructure() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.error").isString())
                .andExpect(jsonPath("$.message").isString());
    }
}