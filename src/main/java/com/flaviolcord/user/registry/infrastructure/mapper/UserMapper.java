package com.flaviolcord.user.registry.infrastructure.mapper;

import com.flaviolcord.user.registry.infrastructure.dto.UserDTO;
import com.flaviolcord.user.registry.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDTO userDTO);

    UserDTO toDTO(User user);
}