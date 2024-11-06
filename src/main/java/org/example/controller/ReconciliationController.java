// src/main/java/org/example/controller/ReconciliationController.java
package org.example.controller;

import org.example.model.Transaction;
import org.example.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reconciliation")
public class ReconciliationController {
    private final VerificationService verificationService;

    @Autowired
    public ReconciliationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/verify")
    public String verifyTransaction(@RequestBody Transaction transaction) {
        // Simulate external data for testing
        Transaction externalData = new Transaction();
        externalData.setTransactionId(transaction.getTransactionId());
        externalData.setPrice(transaction.getPrice() + 10);  // Simulated mismatch

        verificationService.verifyTransaction(transaction, externalData);
        return "Verification completed. Check logs for mismatches.";
    }
}
