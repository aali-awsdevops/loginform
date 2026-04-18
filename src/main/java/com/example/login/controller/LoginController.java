package com.example.login.controller;

import com.example.login.dto.ForgotPasswordRequest;
import com.example.login.dto.LoginRequest;
import com.example.login.dto.LoginResponse;
import com.example.login.dto.PasswordResetRequest;
import com.example.login.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Endpoints for login and password reset")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Operation(summary = "Login user", description = "Authenticate a user with username and password. Use the reset-password endpoint to change a password with a valid reset token.")
    @PostMapping
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login attempt for username={}", request.getUsername());
        boolean valid = loginService.authenticate(request.getUsername(), request.getPassword());
        if (valid) {
            logger.info("Login successful for username={}", request.getUsername());
            return ResponseEntity.ok(new LoginResponse("success", "Login successful"));
        }
        logger.warn("Login failed for username={}", request.getUsername());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse("error", "Invalid username or password"));
    }

    @Operation(summary = "Request password reset", description = "Generate a password reset token for a user. This token is not the login password; use it only with /api/login/reset-password.")
    @PostMapping("/forgot-password")
    public ResponseEntity<LoginResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        logger.info("Password reset requested for username={}", request.getUsername());
        return loginService.createPasswordResetToken(request.getUsername())
                .map(token -> {
                    logger.info("Password reset token generated for username={}", request.getUsername());
                    return ResponseEntity.ok(new LoginResponse("success", "Password reset token created: " + token + ". Use /api/login/reset-password with this token and a new password."));
                })
                .orElseGet(() -> {
                    logger.warn("Password reset request failed: username not found={}", request.getUsername());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new LoginResponse("error", "Username not found"));
                });
    }

    @Operation(summary = "Reset password", description = "Reset a user password using a valid token")
    @PostMapping("/reset-password")
    public ResponseEntity<LoginResponse> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        boolean updated = loginService.resetPassword(request.getUsername(), request.getResetToken(), request.getNewPassword());
        if (updated) {
            return ResponseEntity.ok(new LoginResponse("success", "Password updated successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new LoginResponse("error", "Invalid token, expired token, or username"));
    }
}
