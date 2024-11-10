package org.example.service;

import org.example.model.MismatchLog;
import org.example.repository.MismatchLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final MismatchLogRepository mismatchLogRepository;

    @Autowired
    public AlertService(MismatchLogRepository mismatchLogRepository) {
        this.mismatchLogRepository = mismatchLogRepository;
    }

    // Generate alerts for a specific transaction ID, including Yahoo Finance comparison alerts
    public List<String> generateAlertsForTransaction(String transactionId) {
        List<MismatchLog> mismatches = mismatchLogRepository.findByTransactionId(transactionId);
        return mismatches.stream()
                .map(mismatch -> "Alert: " + mismatch.getDescription() + " | Field: " + mismatch.getField() +
                        " | Expected: " + mismatch.getInternalValue() + " | Found: " + mismatch.getExternalValue() +
                        " | Source: " + mismatch.getSource())
                .collect(Collectors.toList());
    }

    // Fetch all mismatches and compile alerts for admin reporting
    public List<String> generateAdminAlerts() {
        List<MismatchLog> allMismatches = mismatchLogRepository.findAll();
        return allMismatches.stream()
                .map(mismatch -> "Admin Alert: Transaction ID " + mismatch.getTransactionId() + " | " + mismatch.getDescription() +
                        " | Field: " + mismatch.getField() +
                        " | Internal: " + mismatch.getInternalValue() +
                        " | External: " + mismatch.getExternalValue() +
                        " | Source: " + mismatch.getSource())
                .collect(Collectors.toList());
    }

    // Generate summary of all mismatches, categorized by source for the report
    public String generateMismatchSummary() {
        List<MismatchLog> allMismatches = mismatchLogRepository.findAll();
        StringBuilder summary = new StringBuilder("=== Mismatch Summary ===\n");

        allMismatches.forEach(mismatch -> summary.append("Transaction ID: ")
                .append(mismatch.getTransactionId())
                .append(" | Field: ").append(mismatch.getField())
                .append(" | Internal: ").append(mismatch.getInternalValue())
                .append(" | External: ").append(mismatch.getExternalValue())
                .append(" | Source: ").append(mismatch.getSource())
                .append(" | Description: ").append(mismatch.getDescription())
                .append("\n"));

        return summary.toString();
    }

    // Generate pre-execution alert for transaction validation failures
    public void preExecutionAlert(String transactionId, String message) {
        System.out.println("Pre-Execution Alert: Transaction ID " + transactionId + " - " + message);
    }
}
