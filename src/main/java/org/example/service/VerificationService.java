package org.example.service;

import org.example.dto.TransactionDTO;
import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.repository.MismatchLogRepository;
import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VerificationService {

    private final TransactionRepository transactionRepository;
    private final MismatchLogRepository mismatchLogRepository;
    private final ExternalTransactionService externalTransactionService;

    // Thresholds for validation
    @Value("${transaction.price.min:10}")
    private double minPrice;

    @Value("${transaction.price.max:10000}")
    private double maxPrice;

    @Value("${transaction.quantity.min:1}")
    private int minQuantity;

    @Value("${transaction.quantity.max:1000}")
    private int maxQuantity;

    @Autowired
    public VerificationService(TransactionRepository transactionRepository,
                               MismatchLogRepository mismatchLogRepository,
                               ExternalTransactionService externalTransactionService) {
        this.transactionRepository = transactionRepository;
        this.mismatchLogRepository = mismatchLogRepository;
        this.externalTransactionService = externalTransactionService;
    }

    // Verify transactions against internal records and log mismatches
    public List<MismatchLog> verifyTransactions(List<Transaction> externalTransactions, String source) {
        List<MismatchLog> mismatches = new ArrayList<>();

        for (Transaction externalTransaction : externalTransactions) {
            Optional<Transaction> internalTransactionOpt = transactionRepository.findByTransactionId(externalTransaction.getTransactionId());

            if (internalTransactionOpt.isPresent()) {
                Transaction internalTransaction = internalTransactionOpt.get();

                // Compare fields and log mismatches
                checkForMismatch(internalTransaction.getTransactionId(), "price", internalTransaction.getPrice(), externalTransaction.getPrice(), source, mismatches, "Price mismatch detected");
                checkForMismatch(internalTransaction.getTransactionId(), "quantity", internalTransaction.getQuantity(), externalTransaction.getQuantity(), source, mismatches, "Quantity mismatch detected");
                checkForMismatch(internalTransaction.getTransactionId(), "UID", internalTransaction.getUid(), externalTransaction.getUid(), source, mismatches, "UID mismatch detected");
            } else {
                mismatches.add(logMismatch(externalTransaction.getTransactionId(), "status", "missing", "new external transaction", source, "No matching internal transaction found"));
            }
        }
        mismatchLogRepository.saveAll(mismatches);  // Save all mismatches at once for efficiency
        return mismatches;
    }

    private void checkForMismatch(String transactionId, String field, Object internalValue, Object externalValue, String source, List<MismatchLog> mismatches, String description) {
        if (!internalValue.equals(externalValue)) {
            mismatches.add(logMismatch(transactionId, field, String.valueOf(internalValue), String.valueOf(externalValue), source, description));
        }
    }

    private MismatchLog logMismatch(String transactionId, String field, String internalValue, String externalValue, String source, String description) {
        MismatchLog mismatchLog = new MismatchLog(transactionId, field, internalValue, externalValue, source, description);
        mismatchLogRepository.save(mismatchLog);  // Save mismatch to the database
        return mismatchLog;  // Return the logged mismatch
    }

    // Pre-execution validation for fat finger errors and range checks
    public boolean validateTransactionPreExecution(Transaction transaction) {
        boolean isValid = validateTransactionRange(transaction);

        if (!isValid) {
            System.out.println("Alert: Potential input error for transaction ID " + transaction.getTransactionId() +
                    ". Check price and quantity ranges before execution.");
        }

        return isValid;
    }

    // Validate transaction data against predefined range boundaries
    public boolean validateTransactionRange(Transaction transaction) {
        return transaction.getPrice() >= minPrice && transaction.getPrice() <= maxPrice
                && transaction.getQuantity() >= minQuantity && transaction.getQuantity() <= maxQuantity;
    }

    // Retrieve all mismatches for post-execution analysis
    public List<MismatchLog> getAllMismatches() {
        return mismatchLogRepository.findAll();
    }

    // Generate a detailed mismatch report, providing a breakdown by source
    public void generateDetailedMismatchReport() {
        List<MismatchLog> mismatches = getAllMismatches();
        System.out.println("==== Mismatch Report ====");
        mismatches.forEach(mismatch -> System.out.println(
                "Transaction ID: " + mismatch.getTransactionId() +
                        ", Field: " + mismatch.getField() +
                        ", Internal Value: " + mismatch.getInternalValue() +
                        ", External Value: " + mismatch.getExternalValue() +
                        ", Source: " + mismatch.getSource()
        ));
    }

    // Fetch mismatches specific to a transaction for reporting
    public List<MismatchLog> getMismatchesByTransactionId(String transactionId) {
        return mismatchLogRepository.findByTransactionId(transactionId);
    }
}
