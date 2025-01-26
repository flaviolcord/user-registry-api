package com.flaviolcord.user.registry.infrastructure.mapper;

import com.flaviolcord.user.registry.infrastructure.dto.UserDTO;
import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.infrastructure.persistence.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Domain Model <-> DTO
    User toDomainModel(UserDTO userDTO);
    UserDTO toDTO(User user);

    // Domain Model <-> JPA Entity
    UserEntity toEntity(User user);
    User toDomainModel(UserEntity userEntity);
}