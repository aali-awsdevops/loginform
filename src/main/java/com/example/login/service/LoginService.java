package com.example.login.service;

import com.example.login.model.User;
import com.example.login.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);
    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authenticate(String username, String password) {
        logger.debug("Authenticating user={}", username);
        boolean authenticated = userRepository.findByUsername(username)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
        if (authenticated) {
            logger.info("Authentication succeeded for username={}", username);
        } else {
            logger.warn("Authentication failed for username={}", username);
        }
        return authenticated;
    }

    public Optional<String> createPasswordResetToken(String username) {
        logger.debug("Creating password reset token for username={}", username);
        return userRepository.findByUsername(username).map(user -> {
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);
            logger.info("Password reset token generated for username={}; expires at={}", username, user.getResetTokenExpiry());
            return resetToken;
        });
    }

    public boolean resetPassword(String username, String token, String newPassword) {
        logger.debug("Reset password attempt for username={}", username);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            logger.warn("Password reset failed: username not found={}", username);
            return false;
        }

        User user = optionalUser.get();
        if (!token.equals(user.getResetToken())) {
            logger.warn("Password reset failed: invalid token for username={}", username);
            return false;
        }
        if (user.getResetTokenExpiry() == null || !user.getResetTokenExpiry().isAfter(LocalDateTime.now())) {
            logger.warn("Password reset failed: token expired for username={}", username);
            return false;
        }

        user.setPassword(newPassword);
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        logger.info("Password reset successful for username={}", username);
        return true;
    }
}
