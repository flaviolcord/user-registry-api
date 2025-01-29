package com.flaviolcord.user.registry;

import com.flaviolcord.user.registry.infrastructure.config.UserProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableConfigurationProperties(UserProperties.class)
public class UserRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserRegistryApplication.class, args);
	}
}
