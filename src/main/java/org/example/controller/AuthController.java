package org.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, String>> authenticate() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }
}