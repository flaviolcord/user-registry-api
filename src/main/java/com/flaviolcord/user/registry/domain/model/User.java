package com.flaviolcord.user.registry.domain.model;

import java.time.LocalDate;

public record User(
        Long id,
        String username,
        LocalDate birthdate,
        String countryOfResidence,
        String phoneNumber,
        String gender
){}