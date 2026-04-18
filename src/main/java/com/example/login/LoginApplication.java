package com.example.login;

import com.example.login.model.User;
import com.example.login.repository.UserRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Login API", version = "v1", description = "Authentication service with Swagger documentation"))
public class LoginApplication {
    private static final Logger logger = LoggerFactory.getLogger(LoginApplication.class);

    public static void main(String[] args) {
        logger.info("Starting LoginApplication");
        SpringApplication.run(LoginApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDefaultUser(UserRepository userRepository) {
        return args -> {
            logger.info("Checking default user initialization");
            long existingUsers = userRepository.count();
            if (existingUsers == 0) {
                userRepository.save(new User("admin", "P@ssw0rd"));
                logger.info("Default user 'admin' created because no users existed");
            } else {
                logger.info("Default user initialization skipped; existing users found={}", existingUsers);
            }
        };
    }
}
