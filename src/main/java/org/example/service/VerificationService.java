package org.example.service;

import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.repository.MismatchLogRepository;
import org.example.repository.TransactionRepository;
import org.example.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VerificationService {

    private final TransactionRepository transactionRepository;
    private final MismatchLogRepository mismatchLogRepository;

    // Range values to validate transactions for potential input errors
    @Value("${transaction.price.min:10}")
    private double minPrice;

    @Value("${transaction.price.max:10000}")
    private double maxPrice;

    @Value("${transaction.quantity.min:1}")
    private int minQuantity;

    @Value("${transaction.quantity.max:1000}")
    private int maxQuantity;

    public VerificationService(TransactionRepository transactionRepository, MismatchLogRepository mismatchLogRepository) {
        this.transactionRepository = transactionRepository;
        this.mismatchLogRepository = mismatchLogRepository;
    }

    // Verify transactions against external sources and log any mismatches
    public void verifyTransactions(List<Transaction> externalTransactions, String source) {
        for (Transaction externalTransaction : externalTransactions) {
            Optional<Transaction> internalTransactionOpt = transactionRepository.findByTransactionId(externalTransaction.getTransactionId());

            if (internalTransactionOpt.isPresent()) {
                Transaction internalTransaction = internalTransactionOpt.get();

                // Compare fields and log mismatches
                if (internalTransaction.getPrice() != externalTransaction.getPrice()) {
                    logMismatch(internalTransaction.getTransactionId(), "price", String.valueOf(internalTransaction.getPrice()),
                            String.valueOf(externalTransaction.getPrice()), source);
                }
                if (internalTransaction.getQuantity() != externalTransaction.getQuantity()) {
                    logMismatch(internalTransaction.getTransactionId(), "quantity", String.valueOf(internalTransaction.getQuantity()),
                            String.valueOf(externalTransaction.getQuantity()), source);
                }
                if (!internalTransaction.getUid().equals(externalTransaction.getUid())) {
                    logMismatch(internalTransaction.getTransactionId(), "UID", internalTransaction.getUid(), externalTransaction.getUid(), source);
                }
            } else {
                logMismatch(externalTransaction.getTransactionId(), "status", "missing", "new external transaction", source);
            }
        }
    }

    // Helper to log mismatches
    private void logMismatch(String transactionId, String field, String internalValue, String externalValue, String source) {
        MismatchLog mismatchLog = new MismatchLog(transactionId, field, internalValue, externalValue, source);
        mismatchLogRepository.save(mismatchLog);
        System.out.println("Logged mismatch from source [" + source + "] in field " + field + " for transaction " + transactionId);
    }

    // Pre-execution validation for range-bound input errors
    public boolean validateTransactionPreExecution(Transaction transaction) {
        boolean isValid = ValidationUtil.validateTransactionRange(transaction, minPrice, maxPrice, minQuantity, maxQuantity);

        if (!isValid) {
            System.out.println("Alert: Potential input error for transaction ID " + transaction.getTransactionId() +
                    ". Check price and quantity ranges before execution.");
        }

        return isValid;
    }

    // Retrieve all mismatches for post-execution analysis
    public List<MismatchLog> getAllMismatches() {
        return mismatchLogRepository.findAll();
    }

    // Generate a detailed mismatch report
    public void generateDetailedMismatchReport() {
        List<MismatchLog> mismatches = getAllMismatches();
        mismatches.forEach(mismatch -> System.out.println("Mismatch Report - Transaction ID: " + mismatch.getTransactionId()
                + ", Field: " + mismatch.getField()
                + ", Internal Value: " + mismatch.getInternalValue()
                + ", External Value: " + mismatch.getExternalValue()
                + ", Source: " + mismatch.getSource()));
    }

    // Method to fetch mismatches specific to a transaction
    public List<MismatchLog> getMismatchesByTransactionId(String transactionId) {
        return mismatchLogRepository.findByTransactionId(transactionId);
    }
}
