package org.example.controller;

import org.example.dto.OptionDTO;
import org.example.dto.TransactionDTO;
import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.service.AlertService;
import org.example.service.ExternalTransactionService;
import org.example.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/reconciliation")
public class ReconciliationController {

    private static final Logger LOGGER = Logger.getLogger(ReconciliationController.class.getName());

    private final VerificationService verificationService;
    private final AlertService alertService;
    private final ExternalTransactionService externalTransactionService;

    @Autowired
    public ReconciliationController(VerificationService verificationService, AlertService alertService, ExternalTransactionService externalTransactionService) {
        this.verificationService = verificationService;
        this.alertService = alertService;
        this.externalTransactionService = externalTransactionService;
    }

    // Admin endpoint to verify transactions with Yahoo Finance data integration
    @PostMapping("/admin/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VerificationResponse> verifyTransactionsAdmin(@RequestBody List<TransactionDTO> externalTransactions) {
        List<Transaction> transactions = externalTransactions.stream()
                .map(dto -> new Transaction(
                        dto.getTransactionId(),
                        dto.getUid(),
                        dto.getPrice().doubleValue(),  // Cast to primitive double
                        dto.getQuantity().intValue(),  // Cast to primitive int
                        dto.getStatus(),
                        dto.getSymbol()))
                .collect(Collectors.toList());

        List<MismatchLog> mismatches = verificationService.verifyTransactions(transactions, "external_source");
        List<String> stockMismatches = externalTransactionService.compareWithStockData(transactions);

        VerificationResponse response = new VerificationResponse();
        response.setMessage(mismatches.isEmpty() && stockMismatches.isEmpty() ? "Verification complete. No mismatches detected." : "Verification complete. Mismatches detected.");
        response.setMismatches(mismatches);
        response.setStockMismatches(stockMismatches);

        LOGGER.log(Level.INFO, "Admin transaction verification complete with {0} mismatches.", mismatches.size() + stockMismatches.size());

        return ResponseEntity.ok(response);
    }

    // User endpoint for simpler verification without stock data
    @PostMapping("/user/verify")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<VerificationResponse> verifyTransactionsUser(@RequestBody List<TransactionDTO> externalTransactions) {
        List<Transaction> transactions = externalTransactions.stream()
                .map(dto -> new Transaction(
                        dto.getTransactionId(),
                        dto.getUid(),
                        dto.getPrice().doubleValue(),  // Cast to primitive double
                        dto.getQuantity().intValue(),  // Cast to primitive int
                        dto.getStatus()))
                .collect(Collectors.toList());

        List<MismatchLog> mismatches = verificationService.verifyTransactions(transactions, "user_source");

        VerificationResponse response = new VerificationResponse();
        response.setMessage(mismatches.isEmpty() ? "User verification complete. No mismatches detected." : "User verification complete. Mismatches detected.");
        response.setMismatches(mismatches);

        LOGGER.log(Level.INFO, "User transaction verification complete with {0} mismatches.", mismatches.size());

        return ResponseEntity.ok(response);
    }


    // Admin-only endpoint for generating a detailed mismatch report
    @GetMapping("/admin/mismatch-report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateMismatchReport() {
        verificationService.generateDetailedMismatchReport();
        LOGGER.log(Level.INFO, "Mismatch report generated.");
        return ResponseEntity.ok("Mismatch report generated in logs.");
    }

    // Open endpoint to fetch most active options from Yahoo Finance
    @GetMapping("/active-options")
    public ResponseEntity<List<OptionDTO>> getMostActiveOptions() {
        List<OptionDTO> activeOptions = externalTransactionService.fetchMostActiveOptions();
        LOGGER.log(Level.INFO, "Most active options fetched from Yahoo Finance.");
        return ResponseEntity.ok(activeOptions);
    }

    // User-specific endpoint to retrieve alerts for a specific transaction
    @GetMapping("/user/alerts/{transactionId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<String>> getAlertsForTransaction(@PathVariable String transactionId) {
        List<String> alerts = alertService.generateAlertsForTransaction(transactionId);
        LOGGER.log(Level.INFO, "Alerts retrieved for transaction ID: {0}", transactionId);
        return alerts.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(alerts);
    }

    // Admin-only endpoint to retrieve all mismatch alerts
    @GetMapping("/admin/alerts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MismatchLog>> getAllAlerts() {
        List<MismatchLog> mismatches = verificationService.getAllMismatches();
        LOGGER.log(Level.INFO, "All mismatch alerts retrieved.");
        return ResponseEntity.ok(mismatches);
    }

    // Inner class to represent the verification response structure
    public static class VerificationResponse {
        private String message;
        private List<MismatchLog> mismatches;
        private List<String> stockMismatches;

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

        public List<String> getStockMismatches() {
            return stockMismatches;
        }

        public void setStockMismatches(List<String> stockMismatches) {
            this.stockMismatches = stockMismatches;
        }
    }
}
