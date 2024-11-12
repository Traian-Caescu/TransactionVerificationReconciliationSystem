package org.example.controller;

import org.example.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing alert-related endpoints.
 * Provides endpoints for retrieving alerts specific to transactions and for admin-level alerts.
 */
@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    /**
     * Constructor for AlertController.
     *
     * @param alertService the service that handles alert-related operations.
     */
    @Autowired
    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    /**
     * Retrieves alerts for a specific transaction based on its ID.
     *
     * @param transactionId the ID of the transaction to get alerts for.
     * @return a list of alert messages related to the specified transaction.
     */
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<String>> getTransactionAlerts(@PathVariable String transactionId) {
        return ResponseEntity.ok(alertService.generateAlertsForTransaction(transactionId));
    }

    /**
     * Retrieves admin-level alerts, which provide a comprehensive overview of all critical alerts.
     *
     * @return a list of alert messages intended for administrative purposes.
     */
    @GetMapping("/admin")
    public ResponseEntity<List<String>> getAdminAlerts() {
        return ResponseEntity.ok(alertService.generateAdminAlerts());
    }
}
