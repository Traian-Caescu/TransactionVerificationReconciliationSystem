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
                        dto.getPrice().doubleValue(),
                        dto.getQuantity().intValue(),
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

    // Endpoint to fetch the most active options from Yahoo Finance
    @GetMapping("/active-options")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OptionDTO>> getMostActiveOptions() {
        List<OptionDTO> activeOptions = externalTransactionService.fetchMostActiveOptions();
        LOGGER.log(Level.INFO, "Most active options fetched from Yahoo Finance.");
        return ResponseEntity.ok(activeOptions);
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
