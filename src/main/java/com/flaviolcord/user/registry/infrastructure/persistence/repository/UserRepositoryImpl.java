package com.flaviolcord.user.registry.infrastructure.persistence.repository;

import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.domain.repository.UserRepository;
import com.flaviolcord.user.registry.infrastructure.mapper.UserMapper;
import com.flaviolcord.user.registry.infrastructure.persistence.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementation of the UserRepository interface.
 * Bridges the domain model with JPA persistence using mappers.
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    /**
     * {@inheritDoc}
     *
     * Maps the domain User to a UserEntity, persists it,
     * and maps the result back to a domain User.
     */
    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(entity);

        return userMapper.toDomainModel(savedEntity);
    }

    /**
     * {@inheritDoc}
     *
     * Retrieves a UserEntity by ID and maps it to a domain User if found.
     */
    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id)
                .map(userMapper::toDomainModel);
    }

    /**
     * {@inheritDoc}
     *
     * Retrieves a UserEntity by username and maps it to a domain User if found.
     */
    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(userMapper::toDomainModel);
    }
}