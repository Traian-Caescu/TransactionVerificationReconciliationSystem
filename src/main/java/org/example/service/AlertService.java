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

    public List<String> generateAlertsForTransaction(String transactionId) {
        List<MismatchLog> mismatches = mismatchLogRepository.findByTransactionId(transactionId);
        return mismatches.stream()
                .map(this::formatAlertMessage)
                .collect(Collectors.toList());
    }

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

    public void preExecutionAlert(String transactionId, String message) {
        System.out.println("Pre-Execution Alert: Transaction ID " + transactionId + " - " + message);
    }

    public void logRejectedTransaction(String transactionId, String field, String internalValue, String source) {
        MismatchLog mismatchLog = new MismatchLog(
                transactionId,
                field,
                internalValue,
                "Rejected",
                source,
                "Transaction rejected due to role-specific validation"
        );
        mismatchLogRepository.save(mismatchLog);
        System.out.println("Alert: Rejected transaction logged for Transaction ID " + transactionId);
    }

    private String formatAlertMessage(MismatchLog mismatch) {
        return "Alert: " + mismatch.getDescription() + " | Field: " + mismatch.getField() +
                " | Expected: " + mismatch.getInternalValue() +
                " | Found: " + mismatch.getExternalValue() +
                " | Source: " + mismatch.getSource();
    }
}
