package com.flaviolcord.user.registry.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.application.service.UserService;
import com.flaviolcord.user.registry.infrastructure.dto.UserDTO;
import com.flaviolcord.user.registry.infrastructure.mapper.UserMapper;
import com.flaviolcord.user.registry.infrastructure.exception.UserNotFoundException;
import com.flaviolcord.user.registry.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Add the exception handler
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // for LocalDate serialization
    }

    @Test
    void registerUser_ValidData_ShouldReturnCreated() throws Exception {
        // Arrange
        UserDTO inputDto = new UserDTO();
        inputDto.setUsername("testUser");
        inputDto.setBirthdate(LocalDate.of(1990, 1, 1));
        inputDto.setCountryOfResidence("Brazil");
        inputDto.setPhoneNumber("1234567890");
        inputDto.setGender("Male");

        User domainUser = new User(
                null,
                inputDto.getUsername(),
                inputDto.getBirthdate(),
                inputDto.getCountryOfResidence(),
                inputDto.getPhoneNumber(),
                inputDto.getGender()
        );

        User registeredUser = new User(
                1L,
                inputDto.getUsername(),
                inputDto.getBirthdate(),
                inputDto.getCountryOfResidence(),
                inputDto.getPhoneNumber(),
                inputDto.getGender()
        );

        UserDTO responseDto = new UserDTO();
        responseDto.setId(1L);
        responseDto.setUsername(inputDto.getUsername());
        responseDto.setBirthdate(inputDto.getBirthdate());
        responseDto.setCountryOfResidence(inputDto.getCountryOfResidence());
        responseDto.setPhoneNumber(inputDto.getPhoneNumber());
        responseDto.setGender(inputDto.getGender());

        when(userMapper.toDomainModel(any(UserDTO.class))).thenReturn(domainUser);
        when(userService.registerUser(any(User.class))).thenReturn(registeredUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.country_of_residence").value("Brazil"))
                .andExpect(jsonPath("$.phone_number").value("1234567890"))
                .andExpect(jsonPath("$.gender").value("Male"));
    }

    @Test
    void registerUser_InvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserDTO invalidDto = new UserDTO();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ExistingUser_ShouldReturnUser() throws Exception {
        // Arrange
        Long userId = 1L;
        User user = new User(
                userId,
                "testUser",
                LocalDate.of(1990, 1, 1),
                "Brazil",
                "1234567890",
                "Male"
        );

        UserDTO responseDto = new UserDTO();
        responseDto.setId(userId);
        responseDto.setUsername("testUser");
        responseDto.setBirthdate(LocalDate.of(1990, 1, 1));
        responseDto.setCountryOfResidence("Brazil");
        responseDto.setPhoneNumber("1234567890");
        responseDto.setGender("Male");

        when(userService.findUserById(userId)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.country_of_residence").value("Brazil"))
                .andExpect(jsonPath("$.phone_number").value("1234567890"))
                .andExpect(jsonPath("$.gender").value("Male"));
    }

    @Test
    void getUserById_NonExistingUser_ShouldReturnNotFound() throws Exception {
        // Arrange
        Long userId = 999L;
        when(userService.findUserById(userId))
                .thenThrow(new UserNotFoundException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("User not found"))
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}