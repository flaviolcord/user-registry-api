package com.flaviolcord.user.registry.infrastructure.mapper;

import com.flaviolcord.user.registry.infrastructure.dto.UserDTO;
import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.infrastructure.persistence.UserEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapUserDTOToDomainModel() {
        // Arrange
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("testUser");
        dto.setBirthdate(LocalDate.of(1990, 1, 1));
        dto.setCountryOfResidence("Brazil");
        dto.setPhoneNumber("1234567890");
        dto.setGender("Male");

        // Act
        User user = mapper.toDomainModel(dto);

        // Assert
        assertAll(
                () -> assertEquals(dto.getId(), user.id()),
                () -> assertEquals(dto.getUsername(), user.username()),
                () -> assertEquals(dto.getBirthdate(), user.birthdate()),
                () -> assertEquals(dto.getCountryOfResidence(), user.countryOfResidence()),
                () -> assertEquals(dto.getPhoneNumber(), user.phoneNumber()),
                () -> assertEquals(dto.getGender(), user.gender())
        );
    }

    @Test
    void shouldMapDomainModelToDTO() {
        // Arrange
        User user = new User(
                1L,
                "testUser",
                LocalDate.of(1990, 1, 1),
                "Brazil",
                "1234567890",
                "Male"
        );

        // Act
        UserDTO dto = mapper.toDTO(user);

        // Assert
        assertAll(
                () -> assertEquals(user.id(), dto.getId()),
                () -> assertEquals(user.username(), dto.getUsername()),
                () -> assertEquals(user.birthdate(), dto.getBirthdate()),
                () -> assertEquals(user.countryOfResidence(), dto.getCountryOfResidence()),
                () -> assertEquals(user.phoneNumber(), dto.getPhoneNumber()),
                () -> assertEquals(user.gender(), dto.getGender())
        );
    }

    @Test
    void shouldMapDomainModelToEntity() {
        // Arrange
        User user = new User(
                1L,
                "testUser",
                LocalDate.of(1990, 1, 1),
                "Brazil",
                "1234567890",
                "Male"
        );

        // Act
        UserEntity entity = mapper.toEntity(user);

        // Assert
        assertAll(
                () -> assertEquals(user.id(), entity.getId()),
                () -> assertEquals(user.username(), entity.getUsername()),
                () -> assertEquals(user.birthdate(), entity.getBirthdate()),
                () -> assertEquals(user.countryOfResidence(), entity.getCountryOfResidence()),
                () -> assertEquals(user.phoneNumber(), entity.getPhoneNumber()),
                () -> assertEquals(user.gender(), entity.getGender())
        );
    }

    @Test
    void shouldMapEntityToDomainModel() {
        // Arrange
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setUsername("testUser");
        entity.setBirthdate(LocalDate.of(1990, 1, 1));
        entity.setCountryOfResidence("Brazil");
        entity.setPhoneNumber("1234567890");
        entity.setGender("Male");

        // Act
        User user = mapper.toDomainModel(entity);

        // Assert
        assertAll(
                () -> assertEquals(entity.getId(), user.id()),
                () -> assertEquals(entity.getUsername(), user.username()),
                () -> assertEquals(entity.getBirthdate(), user.birthdate()),
                () -> assertEquals(entity.getCountryOfResidence(), user.countryOfResidence()),
                () -> assertEquals(entity.getPhoneNumber(), user.phoneNumber()),
                () -> assertEquals(entity.getGender(), user.gender())
        );
    }

    @Test
    void shouldHandleNullValues() {
        // Arrange
        UserDTO dto = new UserDTO();
        // Leave all fields null

        // Act
        User user = mapper.toDomainModel(dto);

        // Assert
        assertAll(
                () -> assertNull(user.id()),
                () -> assertNull(user.username()),
                () -> assertNull(user.birthdate()),
                () -> assertNull(user.countryOfResidence()),
                () -> assertNull(user.phoneNumber()),
                () -> assertNull(user.gender())
        );
    }
}