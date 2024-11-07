package org.example.service;

import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.repository.MismatchLogRepository;
import org.example.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VerificationService {

    private final TransactionRepository transactionRepository;
    private final MismatchLogRepository mismatchLogRepository;

    public VerificationService(TransactionRepository transactionRepository, MismatchLogRepository mismatchLogRepository) {
        this.transactionRepository = transactionRepository;
        this.mismatchLogRepository = mismatchLogRepository;
    }

    // Method to verify transactions against external data
    public void verifyTransactions(List<Transaction> externalTransactions) {
        for (Transaction externalTransaction : externalTransactions) {
            Transaction internalTransaction = transactionRepository.findByTransactionId(externalTransaction.getTransactionId()).orElse(null);

            if (internalTransaction != null) {
                // Compare fields and log mismatches
                if (internalTransaction.getPrice() != externalTransaction.getPrice()) {
                    logMismatch(internalTransaction.getTransactionId(), "price", String.valueOf(internalTransaction.getPrice()), String.valueOf(externalTransaction.getPrice()));
                }
                if (internalTransaction.getQuantity() != externalTransaction.getQuantity()) {
                    logMismatch(internalTransaction.getTransactionId(), "quantity", String.valueOf(internalTransaction.getQuantity()), String.valueOf(externalTransaction.getQuantity()));
                }
                if (!internalTransaction.getUid().equals(externalTransaction.getUid())) {
                    logMismatch(internalTransaction.getTransactionId(), "UID", internalTransaction.getUid(), externalTransaction.getUid());
                }
            }
        }
    }

    // Helper method to log mismatches
    private void logMismatch(String transactionId, String field, String internalValue, String externalValue) {
        MismatchLog mismatchLog = new MismatchLog(transactionId, field, internalValue, externalValue);
        mismatchLogRepository.save(mismatchLog);
    }

    // Method to retrieve all mismatch logs
    public List<MismatchLog> getAllMismatches() {
        return mismatchLogRepository.findAll();
    }
}
