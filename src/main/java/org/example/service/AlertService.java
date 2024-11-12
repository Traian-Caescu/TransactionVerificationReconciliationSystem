package org.example.service;

import org.example.model.MismatchLog;
import org.example.repository.MismatchLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Service for managing alerts related to transactions and mismatches.
 * It generates alerts for transactions based on mismatches and other events.
 */
@Service
public class AlertService {

    private final MismatchLogRepository mismatchLogRepository;
    private static final Logger LOGGER = Logger.getLogger(AlertService.class.getName());

    @Autowired
    public AlertService(MismatchLogRepository mismatchLogRepository) {
        this.mismatchLogRepository = mismatchLogRepository;
    }

    /**
     * Generate alerts for a specific transaction by its ID.
     *
     * @param transactionId the ID of the transaction.
     * @return a list of formatted alert messages for the transaction.
     */
    public List<String> generateAlertsForTransaction(String transactionId) {
        List<MismatchLog> mismatches = mismatchLogRepository.findByTransactionId(transactionId);
        return mismatches.stream()
                .map(this::formatAlertMessage)
                .collect(Collectors.toList());
    }

    /**
     * Generate alerts for the admin, summarizing all mismatches.
     *
     * @return a list of formatted alert messages for the admin.
     */
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

    /**
     * Generate a summary of all mismatches in the system.
     *
     * @return a string summary of all mismatches.
     */
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

    /**
     * Pre-execution alert for transaction validation failures or other events.
     *
     * @param transactionId the ID of the transaction.
     * @param message       the alert message.
     */
    public void preExecutionAlert(String transactionId, String message) {
        LOGGER.warning("Pre-Execution Alert: Transaction ID " + transactionId + " - " + message);
    }

    /**
     * Log a rejected transaction with specific mismatch details.
     *
     * @param transactionId the ID of the rejected transaction.
     * @param field         the field that caused the rejection.
     * @param internalValue the internal value of the field.
     * @param source        the source of the rejection.
     */
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
        LOGGER.info("Alert: Rejected transaction logged for Transaction ID " + transactionId);
    }

    /**
     * Format a mismatch into a human-readable alert message.
     *
     * @param mismatch the mismatch log.
     * @return a formatted alert message.
     */
    private String formatAlertMessage(MismatchLog mismatch) {
        return "Alert: " + mismatch.getDescription() + " | Field: " + mismatch.getField() +
                " | Expected: " + mismatch.getInternalValue() +
                " | Found: " + mismatch.getExternalValue() +
                " | Source: " + mismatch.getSource();
    }
}
