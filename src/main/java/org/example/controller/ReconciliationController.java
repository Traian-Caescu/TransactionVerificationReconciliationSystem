package org.example.controller;

import org.example.dto.OptionDTO;
import org.example.dto.TransactionDTO;
import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.example.service.AlertService;
import org.example.service.ExternalTransactionService;
import org.example.service.VerificationService;
import org.example.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller for handling transaction reconciliation and mismatch verification.
 * Supports transaction verification and mismatch logging.
 */
@Controller
@RequestMapping("/api/reconciliation")
public class ReconciliationController {

    private static final Logger LOGGER = Logger.getLogger(ReconciliationController.class.getName());

    private final VerificationService verificationService;
    private final AlertService alertService;
    private final ExternalTransactionService externalTransactionService;
    private final TransactionService transactionService;

    /**
     * Constructor to initialize services.
     *
     * @param verificationService       service for transaction verification.
     * @param alertService              service for alert generation.
     * @param externalTransactionService service for fetching external data (e.g., stock data).
     * @param transactionService        service for transaction management.
     */
    @Autowired
    public ReconciliationController(VerificationService verificationService, AlertService alertService, ExternalTransactionService externalTransactionService, TransactionService transactionService) {
        this.verificationService = verificationService;
        this.alertService = alertService;
        this.externalTransactionService = externalTransactionService;
        this.transactionService = transactionService;
    }

    /**
     * Verifies transactions against internal and external data.
     *
     * @param externalTransactions list of external transactions to verify.
     * @return verification response containing mismatch details or a success message.
     */
    @PostMapping("/verify")
    public ResponseEntity<VerificationResponse> verifyTransactions(@RequestBody List<TransactionDTO> externalTransactions) {
        List<Transaction> transactions = externalTransactions.stream()
                .map(dto -> new Transaction(
                        dto.getTransactionId(),
                        dto.getUid(),
                        dto.getPrice().doubleValue(),
                        dto.getQuantity().intValue(),
                        convertStatus(dto.getStatus()), // Convert String to enum
                        dto.getSymbol()
                ))
                .collect(Collectors.toList());

        String userRole = transactionService.getUserRole();
        List<MismatchLog> mismatches = verificationService.verifyTransactions(transactions, "external_source", userRole);
        List<String> stockMismatches = externalTransactionService.compareWithStockData(transactions);

        VerificationResponse response = new VerificationResponse();
        response.setMessage(mismatches.isEmpty() && stockMismatches.isEmpty() ? "Verification complete. No mismatches detected." : "Verification complete. Mismatches detected.");
        response.setMismatches(mismatches);
        response.setStockMismatches(stockMismatches);

        LOGGER.log(Level.INFO, "Transaction verification complete for {0} role with {1} mismatches.", new Object[]{userRole, mismatches.size() + stockMismatches.size()});

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all mismatches in the system.
     * Accessible only by users with SENIOR role.
     *
     * @return response entity containing the list of mismatches.
     */
    @GetMapping("/mismatches")
    @PreAuthorize("hasRole('SENIOR')")
    public ResponseEntity<List<MismatchLog>> getAllMismatches() {
        List<MismatchLog> mismatches = verificationService.getAllMismatches();
        return ResponseEntity.ok(mismatches);
    }

    /**
     * Retrieves mismatches for a specific transaction by its ID.
     *
     * @param transactionId ID of the transaction.
     * @return response entity with mismatches or not found status.
     */
    @GetMapping("/mismatches/{transactionId}")
    @PreAuthorize("hasRole('SENIOR')")
    public ResponseEntity<?> getMismatchesByTransactionId(@PathVariable String transactionId) {
        List<MismatchLog> mismatches = verificationService.getMismatchesByTransactionId(transactionId);
        if (mismatches.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No mismatches found for transaction ID " + transactionId);
        }
        return ResponseEntity.ok(mismatches);
    }

    /**
     * Fetches the most active options from Yahoo Finance.
     * Accessible only by users with SENIOR role.
     *
     * @return response entity containing a list of most active options.
     */
    @GetMapping("/active-options")
    @PreAuthorize("hasRole('SENIOR')")
    public ResponseEntity<List<OptionDTO>> getMostActiveOptions() {
        List<OptionDTO> activeOptions = externalTransactionService.fetchMostActiveOptions();
        LOGGER.log(Level.INFO, "Most active options fetched from Yahoo Finance.");
        return ResponseEntity.ok(activeOptions);
    }

    /**
     * Converts a string status to a TransactionStatus enum.
     *
     * @param status the status string to convert.
     * @return the corresponding TransactionStatus enum.
     */
    private TransactionStatus convertStatus(String status) {
        try {
            return TransactionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid transaction status: " + status);
        }
    }

    /**
     * Inner class to structure verification response with message and mismatches.
     */
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
