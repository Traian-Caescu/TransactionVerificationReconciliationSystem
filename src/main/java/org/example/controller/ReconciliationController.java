package org.example.controller;

import org.example.dto.OptionDTO; // Import the OptionDTO class
import org.example.dto.TransactionDTO;
import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.service.AlertService;
import org.example.service.ExternalTransactionService;
import org.example.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
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
    public ResponseEntity<?> verifyTransactions(@RequestBody List<TransactionDTO> externalTransactions) {
        List<Transaction> transactions = externalTransactions.stream()
                .map(dto -> new Transaction(dto.getTransactionId(), dto.getPrice(), dto.getQuantity(), dto.getUid(), dto.getStatus()))
                .collect(Collectors.toList());

        List<MismatchLog> mismatches = verificationService.verifyTransactions(transactions, "external_source");

        // Create response object
        VerificationResponse response = new VerificationResponse();
        response.setMessage(mismatches.isEmpty() ? "Verification complete. No mismatches detected." : "Verification complete. Mismatches detected.");
        response.setMismatches(mismatches);

        return ResponseEntity.ok(response);
    }

    // New endpoint to fetch and display most active options
    @GetMapping("/active-options")
    public ResponseEntity<List<OptionDTO>> getMostActiveOptions() {
        List<OptionDTO> activeOptions = externalTransactionService.fetchMostActiveOptions();
        return ResponseEntity.ok(activeOptions);
    }

    // New endpoint to view the alerts page
    @GetMapping("/viewAlerts")
    public String viewAlerts(Model model) {
        List<MismatchLog> mismatches = verificationService.getAllMismatches();
        model.addAttribute("alerts", mismatches);
        return "alerts"; // This will resolve to alerts.html
    }

    // New endpoint to view the most active options page
    @GetMapping("/viewActiveOptions")
    public String viewActiveOptions(Model model) {
        List<OptionDTO> activeOptions = externalTransactionService.fetchMostActiveOptions(); // Fetch active options
        System.out.println("Fetched Active Options: " + activeOptions); // Log the fetched options
        model.addAttribute("activeOptions", activeOptions); // Add to model
        return "active-options"; // Return the view name
    }






    // Inner class to represent the verification response
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
