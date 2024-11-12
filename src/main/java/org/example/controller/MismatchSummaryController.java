package org.example.controller;

import org.example.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for generating mismatch summary reports for transaction reconciliations.
 * Restricted to senior users with access to reconciliation insights.
 */
@RestController
@RequestMapping("/api/reconciliation")
public class MismatchSummaryController {

    private final AlertService alertService;

    /**
     * Constructs the controller with the required services.
     *
     * @param alertService service for generating mismatch alerts and summaries.
     */
    @Autowired
    public MismatchSummaryController(AlertService alertService) {
        this.alertService = alertService;
    }

    /**
     * Retrieves a summary of all mismatches detected in transactions.
     * This endpoint is accessible only to senior users.
     *
     * @return a plain text summary of mismatches as a response entity.
     */
    @GetMapping("/mismatch-summary")
    public ResponseEntity<String> getMismatchSummary() {
        return ResponseEntity.ok(alertService.generateMismatchSummary());
    }
}
