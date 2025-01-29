package com.flaviolcord.user.registry.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserPropertiesIT {

    @Autowired
    private UserProperties userProperties;

    @Test
    void shouldLoadUserProperties() {
        // Assert
        assertNotNull(userProperties.getAllowedCountry());
        assertTrue(userProperties.getMinAge() > 0);
    }

    @Test
    void shouldHaveCorrectValues() {
        // Assert
        assertEquals("France", userProperties.getAllowedCountry());
        assertEquals(18, userProperties.getMinAge());
    }

    @Test
    void shouldBeUsedInValidation() {
        // Verify properties are usable
        assertTrue(userProperties.getAllowedCountry().equalsIgnoreCase("France"));
        assertTrue(userProperties.getMinAge() >= 18);
    }
}
