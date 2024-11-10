package org.example.controller;

import org.example.dto.OptionDTO;
import org.example.dto.TransactionDTO;
import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.service.AlertService;
import org.example.service.ExternalTransactionService;
import org.example.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/reconciliation")
public class ReconciliationController {

    private final VerificationService verificationService;
    private final AlertService alertService;
    private final ExternalTransactionService externalTransactionService;

    @Autowired
    public ReconciliationController(VerificationService verificationService, AlertService alertService, ExternalTransactionService externalTransactionService) {
        this.verificationService = verificationService;
        this.alertService = alertService;
        this.externalTransactionService = externalTransactionService;
    }

    // Endpoint to verify transactions against external data sources
    @PostMapping("/verify")
    public ResponseEntity<VerificationResponse> verifyTransactions(@RequestBody List<TransactionDTO> externalTransactions) {
        List<Transaction> transactions = externalTransactions.stream()
                .map(dto -> new Transaction(dto.getTransactionId(), dto.getUid(), dto.getPrice(), dto.getQuantity(), dto.getStatus()))
                .collect(Collectors.toList());

        List<MismatchLog> mismatches = verificationService.verifyTransactions(transactions, "external_source");

        VerificationResponse response = new VerificationResponse();
        response.setMessage(mismatches.isEmpty() ? "Verification complete. No mismatches detected." : "Verification complete. Mismatches detected.");
        response.setMismatches(mismatches);

        return ResponseEntity.ok(response);
    }

    // Endpoint to fetch and display most active options from external service
    @GetMapping("/active-options")
    public ResponseEntity<List<OptionDTO>> getMostActiveOptions() {
        List<OptionDTO> activeOptions = externalTransactionService.fetchMostActiveOptions();
        return ResponseEntity.ok(activeOptions);
    }

    // View to display transaction mismatch alerts on the frontend
    @GetMapping("/viewAlerts")
    public String viewAlerts(Model model) {
        List<MismatchLog> mismatches = verificationService.getAllMismatches();
        model.addAttribute("alerts", mismatches);
        return "alerts"; // Thymeleaf template for alerts display
    }

    // View to display the most active options on the frontend
    @GetMapping("/viewActiveOptions")
    public String getActiveOptionsPage(Model model) {
        List<OptionDTO> activeOptions = externalTransactionService.fetchMostActiveOptions();
        model.addAttribute("activeOptions", activeOptions);
        return "active-options"; // Thymeleaf template for active options display
    }

    // Endpoint to generate a detailed mismatch summary report
    @GetMapping("/mismatch-report")
    public ResponseEntity<String> generateMismatchReport() {
        verificationService.generateDetailedMismatchReport();
        return ResponseEntity.ok("Mismatch report generated in logs.");
    }

    // Endpoint to retrieve all mismatch alerts related to a specific transaction ID
    @GetMapping("/alerts/{transactionId}")
    public ResponseEntity<List<String>> getAlertsForTransaction(@PathVariable String transactionId) {
        List<String> alerts = alertService.generateAlertsForTransaction(transactionId);
        return alerts.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(alerts);
    }

    // Inner class to represent the verification response structure
    public static class VerificationResponse {
        private String message;
        private List<MismatchLog> mismatches;

        // Getters and Setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<MismatchLog> getMismatches() {
            return mismatches;
        }

        public void setMismatches(List<MismatchLog> mismatches) {
            this.mismatches = mismatches;
        }
    }
}
