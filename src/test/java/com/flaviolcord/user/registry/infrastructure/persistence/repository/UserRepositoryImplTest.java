package com.flaviolcord.user.registry.infrastructure.persistence.repository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.infrastructure.mapper.UserMapper;
import com.flaviolcord.user.registry.infrastructure.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        user = new User(1L, "john_doe", LocalDate.of(1990, 1, 1), "France", "1234567890", "Male");
        userEntity = new UserEntity(1L, "john_doe", LocalDate.of(1990, 1, 1), "France", "1234567890", "Male");
    }

    @Test
    void testSave() {
        // Arrange: Mock the behavior of JpaUserRepository and UserMapper
        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(jpaUserRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDomainModel(userEntity)).thenReturn(user);

        // Act: Call the save method
        User savedUser = userRepository.save(user);

        // Assert: Verify the expected behavior
        assertNotNull(savedUser);
        assertEquals(user.username(), savedUser.username());
        verify(userMapper).toEntity(user);
        verify(jpaUserRepository).save(userEntity);
        verify(userMapper).toDomainModel(userEntity);
    }

    @Test
    void testFindById() {
        // Arrange: Mock the behavior of JpaUserRepository and UserMapper
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomainModel(userEntity)).thenReturn(user);

        // Act: Call the findById method
        Optional<User> foundUser = userRepository.findById(1L);

        // Assert: Verify the expected behavior
        assertTrue(foundUser.isPresent());
        assertEquals(user.username(), foundUser.get().username());
        verify(jpaUserRepository).findById(1L);
        verify(userMapper).toDomainModel(userEntity);
    }

    @Test
    void testFindByUsername() {
        // Arrange: Mock the behavior of JpaUserRepository and UserMapper
        when(jpaUserRepository.findByUsername("john_doe")).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomainModel(userEntity)).thenReturn(user);

        // Act: Call the findByUsername method
        Optional<User> foundUser = userRepository.findByUsername("john_doe");

        // Assert: Verify the expected behavior
        assertTrue(foundUser.isPresent());
        assertEquals(user.username(), foundUser.get().username());
        verify(jpaUserRepository).findByUsername("john_doe");
        verify(userMapper).toDomainModel(userEntity);
    }

    // Additional tests for when no user is found (optional)
    @Test
    void testFindByIdWhenNotFound() {
        // Arrange: Mock JpaUserRepository to return an empty Optional
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.empty());

        // Act: Call findById
        Optional<User> foundUser = userRepository.findById(1L);

        // Assert: Verify that no user was found
        assertFalse(foundUser.isPresent());
        verify(jpaUserRepository).findById(1L);
    }

    @Test
    void testFindByUsernameWhenNotFound() {
        // Arrange: Mock JpaUserRepository to return an empty Optional
        when(jpaUserRepository.findByUsername("john_doe")).thenReturn(Optional.empty());

        // Act: Call findByUsername
        Optional<User> foundUser = userRepository.findByUsername("john_doe");

        // Assert: Verify that no user was found
        assertFalse(foundUser.isPresent());
        verify(jpaUserRepository).findByUsername("john_doe");
    }
}