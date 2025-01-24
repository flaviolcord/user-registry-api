package com.flaviolcord.user.registry.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter @Setter
@ConfigurationProperties(prefix = "user")
public class UserProperties {

    private String allowedCountry;
    private int minAge;
}
