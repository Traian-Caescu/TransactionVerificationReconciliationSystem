package org.example.controller;

import org.example.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    @Autowired
    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<String>> getTransactionAlerts(@PathVariable String transactionId) {
        return ResponseEntity.ok(alertService.generateAlertsForTransaction(transactionId));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<String>> getAdminAlerts() {
        return ResponseEntity.ok(alertService.generateAdminAlerts());
    }
}
