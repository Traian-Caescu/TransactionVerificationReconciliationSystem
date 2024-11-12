package org.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for handling authentication-related endpoints.
 * Manages user login attempts and provides error handling for failed logins.
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());

    /**
     * Endpoint for authenticating users. Always returns a success response for simplicity.
     *
     * @return a JSON object with status, message, and timestamp of the login attempt.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> authenticate() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Login successful");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Global error handler for login-related exceptions. Logs errors and returns a user-friendly message.
     *
     * @param ex the exception that occurred.
     * @return a response entity with failure status and error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleLoginError(Exception ex) {
        LOGGER.log(Level.WARNING, "Login attempt failed", ex);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "failure");
        errorResponse.put("message", "Login failed. Please check your credentials.");
        errorResponse.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}
