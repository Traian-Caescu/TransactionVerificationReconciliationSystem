package org.example.controller;

import org.example.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reconciliation")
public class MismatchSummaryController {

    private final AlertService alertService;

    @Autowired
    public MismatchSummaryController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/mismatch-summary")
    public ResponseEntity<String> getMismatchSummary() {
        return ResponseEntity.ok(alertService.generateMismatchSummary());
    }
}
