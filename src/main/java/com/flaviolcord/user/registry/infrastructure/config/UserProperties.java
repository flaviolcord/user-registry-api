package com.flaviolcord.user.registry.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for user management.
 * Loads properties with the 'user' prefix from the application configuration files.
 * <p>
 * Example application.properties/yaml configuration:
 * <pre>
 * user:
 *   allowed-country: France
 *   min-age: 18
 * </pre>
 */
@Configuration
@Getter @Setter
@ConfigurationProperties(prefix = "user")
public class UserProperties {

    /**
     * The only country allowed for user registration.
     * Users from other countries will be rejected during registration.
     */
    private String allowedCountry;

    /**
     * The minimum age required for user registration.
     * Users younger than this age will be rejected during registration.
     */
    private int minAge;
}
