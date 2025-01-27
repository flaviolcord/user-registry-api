package com.flaviolcord.user.registry.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor
public class UserDTO {

    private Long id;

    @NotNull(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate must be in the past")
    private LocalDate birthdate;

    @NotNull(message = "Country of residence is required")
    @JsonProperty("country_of_residence")
    private String countryOfResidence;

    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    @JsonProperty("phone_number")
    private String phoneNumber; // Optional

    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    private String gender; // Optional
}