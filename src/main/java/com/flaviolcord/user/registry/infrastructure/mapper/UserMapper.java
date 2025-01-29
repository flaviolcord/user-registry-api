package com.flaviolcord.user.registry.infrastructure.mapper;

import com.flaviolcord.user.registry.infrastructure.dto.UserDTO;
import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.infrastructure.persistence.UserEntity;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between different representations of a user.
 * <p>
 * This interface provides methods to map between:
 * <ul>
 *     <li>Domain Model (User) and DTO (UserDTO)</li>
 *     <li>Domain Model (User) and JPA Entity (UserEntity)</li>
 * </ul>
 * The component model is set to "spring" for integration with Spring's dependency injection.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a UserDTO to a User domain model.
     *
     * @param userDTO the UserDTO to convert
     * @return the corresponding User domain model
     */
    User toDomainModel(UserDTO userDTO);

    /**
     * Converts a User domain model to a UserDTO.
     *
     * @param user the User domain model to convert
     * @return the corresponding UserDTO
     */
    UserDTO toDTO(User user);

    /**
     * Converts a User domain model to a UserEntity for persistence in the database.
     *
     * @param user the User domain model to convert
     * @return the corresponding UserEntity
     */
    UserEntity toEntity(User user);

    /**
     * Converts a UserEntity (database representation) to a User domain model.
     *
     * @param userEntity the UserEntity to convert
     * @return the corresponding User domain model
     */
    User toDomainModel(UserEntity userEntity);
}