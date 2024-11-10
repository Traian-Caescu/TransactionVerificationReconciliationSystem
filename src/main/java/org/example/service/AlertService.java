package org.example.service;

import org.example.model.MismatchLog;
import org.example.repository.MismatchLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final MismatchLogRepository mismatchLogRepository;

    public AlertService(MismatchLogRepository mismatchLogRepository) {
        this.mismatchLogRepository = mismatchLogRepository;
    }

    // Generate alerts based on mismatches recorded in the system
    public void generateAlerts(List<MismatchLog> mismatches) {
        mismatches.forEach(mismatch -> {
            String alertMessage = String.format("Mismatch detected in %s for Transaction ID %s. Internal Value: %s, External Value: %s, Source: %s",
                    mismatch.getField(), mismatch.getTransactionId(), mismatch.getInternalValue(), mismatch.getExternalValue(), mismatch.getSource());
            sendAlert(alertMessage);
        });
    }

    // Fetch all mismatches from the repository and generate alerts
    public void generateAllMismatchAlerts() {
        List<MismatchLog> mismatches = mismatchLogRepository.findAll();
        generateAlerts(mismatches);
    }

    // Pre-execution alert for potential errors based on out-of-bound transaction values
    public void preExecutionAlert(String transactionId, String alertMessage) {
        String formattedMessage = String.format("Pre-execution Alert for Transaction ID %s: %s", transactionId, alertMessage);
        sendAlert(formattedMessage);
    }

    // Generate mismatch summary report to provide an overview of all discrepancies
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

    // Generate specific alerts for a given transaction ID
    public List<String> generateAlertsForTransaction(String transactionId) {
        List<MismatchLog> mismatches = mismatchLogRepository.findByTransactionId(transactionId);
        return mismatches.stream().map(mismatch -> {
            String alertMessage = String.format("Mismatch in %s for Transaction ID %s. Internal Value: %s, External Value: %s, Source: %s",
                    mismatch.getField(), mismatch.getTransactionId(), mismatch.getInternalValue(), mismatch.getExternalValue(), mismatch.getSource());
            sendAlert(alertMessage);
            return alertMessage;
        }).collect(Collectors.toList());
    }

    // Helper method to simulate sending an alert, could be replaced with actual email or notification logic
    private void sendAlert(String alertMessage) {
        System.out.println("ALERT: " + alertMessage);
        // Placeholder for real alert/notification logic, such as sending an email or notification
    }
}
