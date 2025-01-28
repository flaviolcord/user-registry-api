package com.flaviolcord.user.registry.infrastructure.persistence.repository;

import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.domain.repository.UserRepository;
import com.flaviolcord.user.registry.infrastructure.mapper.UserMapper;
import com.flaviolcord.user.registry.infrastructure.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User(
                null,
                "testuser",
                LocalDate.of(1990, 1, 1),
                "France",
                "1234567890",
                "Male"
        );
    }

    @Test
    void save_NewUser_ShouldPersistAndReturnUser() {
        // Act
        User savedUser = userRepository.save(validUser);

        // Assert
        assertNotNull(savedUser.id());
        assertEquals(validUser.username(), savedUser.username());
        assertEquals(validUser.birthdate(), savedUser.birthdate());
        assertEquals(validUser.countryOfResidence(), savedUser.countryOfResidence());
        assertEquals(validUser.phoneNumber(), savedUser.phoneNumber());
        assertEquals(validUser.gender(), savedUser.gender());

        // Verify database state
        Optional<UserEntity> persistedEntity = jpaUserRepository.findById(savedUser.id());
        assertTrue(persistedEntity.isPresent());
        assertEquals(validUser.username(), persistedEntity.get().getUsername());
    }

    @Test
    void save_UserWithNullOptionalFields_ShouldPersist() {
        // Arrange
        User userWithNullFields = new User(
                null,
                "testuser",
                LocalDate.of(1990, 1, 1),
                "France",
                null,  // Optional phone number
                null   // Optional gender
        );

        // Act
        User savedUser = userRepository.save(userWithNullFields);

        // Assert
        assertNotNull(savedUser.id());
        assertNull(savedUser.phoneNumber());
        assertNull(savedUser.gender());

        // Verify database state
        Optional<UserEntity> persistedEntity = jpaUserRepository.findById(savedUser.id());
        assertTrue(persistedEntity.isPresent());
        assertNull(persistedEntity.get().getPhoneNumber());
        assertNull(persistedEntity.get().getGender());
    }

    @Test
    void save_DuplicateUsername_ShouldThrowException() {
        // Arrange
        userRepository.save(validUser);

        User duplicateUser = new User(
                null,
                validUser.username(),
                LocalDate.of(1995, 1, 1),
                "France",
                "9876543210",
                "Female"
        );

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(duplicateUser);
            jpaUserRepository.flush(); // Force the persistence
        });
    }

    @Test
    void findById_ExistingUser_ShouldReturnUser() {
        // Arrange
        User savedUser = userRepository.save(validUser);

        // Act
        Optional<User> foundUser = userRepository.findById(savedUser.id());

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.id(), foundUser.get().id());
        assertEquals(savedUser.username(), foundUser.get().username());
        assertEquals(savedUser.birthdate(), foundUser.get().birthdate());
        assertEquals(savedUser.countryOfResidence(), foundUser.get().countryOfResidence());
        assertEquals(savedUser.phoneNumber(), foundUser.get().phoneNumber());
        assertEquals(savedUser.gender(), foundUser.get().gender());
    }

    @Test
    void findById_NonExistingUser_ShouldReturnEmpty() {
        // Arrange
        Long nonExistentId = 999L;

        // Act
        Optional<User> result = userRepository.findById(nonExistentId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findByUsername_ExistingUsername_ShouldReturnUser() {
        // Arrange
        User savedUser = userRepository.save(validUser);

        // Act
        Optional<User> foundUser = userRepository.findByUsername(validUser.username());

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.id(), foundUser.get().id());
        assertEquals(savedUser.username(), foundUser.get().username());
    }

    @Test
    void findByUsername_NonExistingUsername_ShouldReturnEmpty() {
        // Act
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void save_NullRequiredFields_ShouldThrowException() {
        // Arrange
        User userWithNullRequired = new User(
                null,
                null,  // Required username
                null,  // Required birthdate
                null,  // Required country
                "1234567890",
                "Male"
        );

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(userWithNullRequired);
            jpaUserRepository.flush();
        });
    }
}
