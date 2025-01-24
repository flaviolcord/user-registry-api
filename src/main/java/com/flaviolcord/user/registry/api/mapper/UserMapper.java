package com.flaviolcord.user.registry.api.mapper;

import com.flaviolcord.user.registry.api.dto.UserDTO;
import com.flaviolcord.user.registry.api.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDTO userDTO);

    UserDTO toDTO(User user);
}