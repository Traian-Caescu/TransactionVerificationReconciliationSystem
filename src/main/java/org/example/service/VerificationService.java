package org.example.service;

import org.example.dto.TransactionDTO; // Import TransactionDTO for external data
import org.example.dto.OptionDTO; // Import OptionDTO for options data
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
    private final ExternalTransactionService externalTransactionService; // Reference to the external service

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
        this.externalTransactionService = externalTransactionService; // Initialize the external service
    }

    // Verify transactions against internal records and log mismatches
    public List<MismatchLog> verifyTransactions(List<Transaction> externalTransactions, String source) {
        List<MismatchLog> mismatches = new ArrayList<>();

        // Fetch most active options as external transactions
        List<OptionDTO> fetchedExternalTransactions = externalTransactionService.fetchMostActiveOptions();

        // Map OptionDTOs to TransactionDTOs
        List<TransactionDTO> transactionDTOs = fetchedExternalTransactions.stream()
                .map(option -> new TransactionDTO(
                        option.getSymbol(),  // Assuming this is the transaction ID
                        option.getLastPrice(),  // Use last price as price
                        1, // Placeholder for quantity, replace with actual logic if necessary
                        "UID123", // Placeholder for UID, replace with actual logic if necessary
                        "active" // Placeholder for status
                ))
                .collect(Collectors.toList());

        // Combine externalTransactions and fetchedExternalTransactions if necessary
        externalTransactions.addAll(transactionDTOs.stream().map(dto -> new Transaction(
                dto.getTransactionId(),
                dto.getPrice(),
                dto.getQuantity(),
                dto.getUid(),
                dto.getStatus()
        )).collect(Collectors.toList()));

        for (Transaction externalTransaction : externalTransactions) {
            Optional<Transaction> internalTransactionOpt = transactionRepository.findByTransactionId(externalTransaction.getTransactionId());

            if (internalTransactionOpt.isPresent()) {
                Transaction internalTransaction = internalTransactionOpt.get();

                // Compare fields and log mismatches
                checkForMismatch(internalTransaction.getTransactionId(), "price", internalTransaction.getPrice(), externalTransaction.getPrice(), source, mismatches);
                checkForMismatch(internalTransaction.getTransactionId(), "quantity", internalTransaction.getQuantity(), externalTransaction.getQuantity(), source, mismatches);
                checkForMismatch(internalTransaction.getTransactionId(), "UID", internalTransaction.getUid(), externalTransaction.getUid(), source, mismatches);
            } else {
                // Log new external transactions that don't exist in internal records
                mismatches.add(logMismatch(externalTransaction.getTransactionId(), "status", "missing", "new external transaction", source));
            }
        }
        return mismatches;  // Return the list of mismatches
    }

    private void checkForMismatch(String transactionId, String field, Object internalValue, Object externalValue, String source, List<MismatchLog> mismatches) {
        if (!internalValue.equals(externalValue)) {
            mismatches.add(logMismatch(transactionId, field, String.valueOf(internalValue), String.valueOf(externalValue), source));
        }
    }

    private MismatchLog logMismatch(String transactionId, String field, String internalValue, String externalValue, String source) {
        MismatchLog mismatchLog = new MismatchLog(transactionId, field, internalValue, externalValue, source);
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
