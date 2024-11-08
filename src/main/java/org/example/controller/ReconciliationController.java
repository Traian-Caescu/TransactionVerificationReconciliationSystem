package org.example.controller;

import org.example.dto.TransactionDTO;
import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.service.AlertService;
import org.example.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reconciliation")
public class ReconciliationController {

    private final VerificationService verificationService;
    private final AlertService alertService;

    @Autowired
    public ReconciliationController(VerificationService verificationService, AlertService alertService) {
        this.verificationService = verificationService;
        this.alertService = alertService;
    }

    // Endpoint to verify transactions against external data sources
    @PostMapping("/verify")
    public ResponseEntity<String> verifyTransactions(@RequestBody List<TransactionDTO> externalTransactions) {
        List<Transaction> transactions = externalTransactions.stream()
                .map(dto -> new Transaction(dto.getTransactionId(), dto.getPrice(), dto.getQuantity(), dto.getUid(), dto.getStatus()))
                .collect(Collectors.toList());

        verificationService.verifyTransactions(transactions, "external_source");  // assuming external source for context
        return ResponseEntity.ok("Verification complete. Mismatches, if any, have been logged.");
    }

    // Endpoint to retrieve mismatch alerts
    @GetMapping("/alerts")
    public ResponseEntity<List<String>> getMismatchAlerts() {
        List<MismatchLog> mismatches = verificationService.getAllMismatches();
        List<String> alerts = mismatches.stream()
                .map(mismatch -> String.format("Mismatch in %s for Transaction ID %s. Internal: %s, External: %s",
                        mismatch.getField(), mismatch.getTransactionId(), mismatch.getInternalValue(), mismatch.getExternalValue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(alerts);
    }
}
