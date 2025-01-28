package com.flaviolcord.user.registry.infrastructure.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.flaviolcord.user.registry.domain.model.User;
import com.flaviolcord.user.registry.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LoggingAspectIT {

    @Autowired
    private UserService userService;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        // Get Logback Logger
        Logger logger = (Logger) LoggerFactory.getLogger(LoggingAspect.class);

        // Create and start a ListAppender
        listAppender = new ListAppender<>();
        listAppender.start();

        // Add appender to the logger
        logger.addAppender(listAppender);
    }

    @Test
    void shouldLogMethodEntryAndExit() {
        // Arrange
        User user = new User(
                null,
                "testuser",
                LocalDate.now().minusYears(20),
                "France",
                "1234567890",
                "Male"
        );

        // Act
        userService.registerUser(user);

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;

        // Verify method entry log
        assertTrue(logsList.stream()
                .anyMatch(event -> event.getMessage().contains("Entering method")
                        && event.getLevel() == Level.DEBUG));

        // Verify method exit log
        assertTrue(logsList.stream()
                .anyMatch(event -> event.getMessage().contains("Exiting method")
                        && event.getLevel() == Level.DEBUG));

        // Verify execution time is logged
        assertTrue(logsList.stream()
                .anyMatch(event -> event.getMessage().contains("Execution time:")));
    }

    @Test
    void shouldLogExceptionWhenThrown() {
        // Arrange
        User invalidUser = new User(
                null,
                "testuser",
                LocalDate.now().minusYears(10), // Will cause validation exception
                "France",
                "1234567890",
                "Male"
        );

        // Act
        try {
            userService.registerUser(invalidUser);
        } catch (Exception ignored) {
            // Exception expected
        }

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;

        // Verify exception is logged
        assertTrue(logsList.stream()
                .anyMatch(event -> event.getMessage().contains("Exception in method")
                        && event.getLevel() == Level.ERROR));
    }

    @Test
    void shouldSanitizeSensitiveData() {
        // Arrange
        User user = new User(
                null,
                "username",
                LocalDate.now().minusYears(20),
                "France",
                "1234567890", // Should be sanitized
                "Male"
        );

        // Act
        userService.registerUser(user);

        // Assert
        List<ILoggingEvent> logsList = listAppender.list;

        // Verify phone number is sanitized
        assertTrue(logsList.stream()
                .noneMatch(event -> event.getMessage().contains("1234567890")));
    }
}