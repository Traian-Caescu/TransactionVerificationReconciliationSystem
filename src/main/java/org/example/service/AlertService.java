package org.example.service;

import org.example.model.MismatchLog;
import org.example.repository.MismatchLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    private final MismatchLogRepository mismatchLogRepository;

    public AlertService(MismatchLogRepository mismatchLogRepository) {
        this.mismatchLogRepository = mismatchLogRepository;
    }

    // Generate alerts based on mismatches recorded in the system
    public void generateAlerts(List<MismatchLog> mismatches) {
        for (MismatchLog mismatch : mismatches) {
            String alertMessage = String.format("Mismatch detected in %s for Transaction ID %s. Internal Value: %s, External Value: %s, Source: %s",
                    mismatch.getField(), mismatch.getTransactionId(), mismatch.getInternalValue(), mismatch.getExternalValue(), mismatch.getSource());

            // In a real application, this would trigger an email or notification instead of just printing
            System.out.println("ALERT: " + alertMessage);
        }
    }

    // Fetch all mismatches from the repository and generate alerts
    public void generateAllMismatchAlerts() {
        List<MismatchLog> mismatches = mismatchLogRepository.findAll();
        generateAlerts(mismatches);
    }

    // Pre-execution alert for potential errors based on out-of-bound transaction values
    public void preExecutionAlert(String transactionId, String alertMessage) {
        System.out.println("Pre-execution Alert for Transaction ID " + transactionId + ": " + alertMessage);
    }

    // Summary report for trade operations with details of all recorded mismatches
    public void generateMismatchSummaryReport() {
        List<MismatchLog> mismatches = mismatchLogRepository.findAll();
        System.out.println("==== Mismatch Summary Report ====");
        mismatches.forEach(mismatch -> System.out.println(
                "Transaction ID: " + mismatch.getTransactionId() +
                        ", Field: " + mismatch.getField() +
                        ", Internal Value: " + mismatch.getInternalValue() +
                        ", External Value: " + mismatch.getExternalValue() +
                        ", Source: " + mismatch.getSource()
        ));
    }
}
