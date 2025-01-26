package com.flaviolcord.user.registry.domain.repository;

import com.flaviolcord.user.registry.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);
}