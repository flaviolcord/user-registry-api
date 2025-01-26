package com.flaviolcord.user.registry.application.service;

import com.flaviolcord.user.registry.application.usecase.FindUserByIdUseCase;
import com.flaviolcord.user.registry.application.usecase.RegisterUserUseCase;
import com.flaviolcord.user.registry.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class UserService {
    private final RegisterUserUseCase registerUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;

    public User registerUser(User user) {
        return registerUserUseCase.execute(user);
    }

    public User findUserById(Long id) {
        return findUserByIdUseCase.execute(id);
    }
}