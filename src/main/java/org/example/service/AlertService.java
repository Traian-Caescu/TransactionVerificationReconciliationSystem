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

    // Method to generate alerts for mismatches
    public void generateAlerts(List<MismatchLog> mismatches) {
        for (MismatchLog mismatch : mismatches) {
            String alertMessage = String.format("Mismatch in %s for Transaction ID %s. Internal: %s, External: %s",
                    mismatch.getField(), mismatch.getTransactionId(), mismatch.getInternalValue(), mismatch.getExternalValue());

            // In a real system, this would trigger an email, notification, etc.
            System.out.println("ALERT: " + alertMessage);
        }
    }

    // Fetch all mismatches and generate alerts
    public void generateAllMismatchAlerts() {
        List<MismatchLog> mismatches = mismatchLogRepository.findAll();
        generateAlerts(mismatches);
    }
}
