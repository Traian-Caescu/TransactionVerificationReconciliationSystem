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

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> authenticate() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Login successful");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    // Error handling for unauthorized or failed login attempts
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
