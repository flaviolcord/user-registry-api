package com.flaviolcord.user.registry.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaviolcord.user.registry.domain.repository.UserRepository;
import com.flaviolcord.user.registry.infrastructure.dto.UserDTO;
import com.flaviolcord.user.registry.infrastructure.persistence.repository.JpaUserRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserManagementFlowIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @BeforeEach
    void setUp() {
        jpaUserRepository.deleteAll();
        jpaUserRepository.flush();
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

    @Test
    void registrationAndRetrievalFlow() throws Exception {
        // 1. Register new user
        UserDTO userDTO = createValidUserDTO();
        String responseJson = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = objectMapper.readTree(responseJson).get("id").asLong();

        // 2. Retrieve and verify user
        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()));
    }

    @Test
    void validationFlow() throws Exception {
        // 1. Test underage user
        UserDTO underageUser = createValidUserDTO();
        underageUser.setBirthdate(LocalDate.now().minusYears(15));
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(underageUser)))
                .andExpect(status().isBadRequest());

        // 2. Test invalid country
        UserDTO foreignUser = createValidUserDTO();
        foreignUser.setCountryOfResidence("Brazil");
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foreignUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void duplicateUserFlow() throws Exception {
        // 1. Register first user
        UserDTO userDTO = createValidUserDTO();
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        // 2. Attempt duplicate registration
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userNotFoundFlow() throws Exception {
        // Attempt to retrieve non-existent user
        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidInputFlow() throws Exception {
        // 1. Invalid username (too short)
        UserDTO invalidUser = createValidUserDTO();
        invalidUser.setUsername("a");
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        // 2. Invalid phone number
        invalidUser.setUsername("validuser");
        invalidUser.setPhoneNumber("123"); // too short
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void optionalFieldsFlow() throws Exception {
        // Create user with only required fields
        UserDTO minimalUser = new UserDTO();
        minimalUser.setUsername("minimaluser");
        minimalUser.setBirthdate(LocalDate.now().minusYears(20));
        minimalUser.setCountryOfResidence("France");

        String responseJson = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalUser)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = objectMapper.readTree(responseJson).get("id").asLong();

        // Verify optional fields are null
        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone_number").isEmpty())
                .andExpect(jsonPath("$.gender").isEmpty());
    }
}