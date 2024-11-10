package org.example.service;

import org.example.model.Transaction;
import org.example.model.TransactionMismatch;
import org.example.repository.TransactionRepository;
import org.example.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AlertService alertService;

    // Validation thresholds
    @Value("${transaction.price.min:10}")
    private double minPrice;

    @Value("${transaction.price.max:10000}")
    private double maxPrice;

    @Value("${transaction.quantity.min:1}")
    private int minQuantity;

    @Value("${transaction.quantity.max:1000}")
    private int maxQuantity;

    public TransactionService(TransactionRepository transactionRepository, AlertService alertService) {
        this.transactionRepository = transactionRepository;
        this.alertService = alertService;
    }

    // Save a new transaction with pre-execution validation and alerting on failure
    public Transaction saveTransaction(Transaction transaction) {
        if (!validateTransactionPreExecution(transaction)) {
            alertService.preExecutionAlert(transaction.getTransactionId(), "Transaction validation failed.");
            return null;
        }
        return transactionRepository.save(transaction);
    }

    // Retrieve a transaction by ID
    public Optional<Transaction> getTransactionById(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId);
    }

    // Retrieve all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Update an existing transaction with validation and alerting on failure
    public Transaction updateTransaction(String transactionId, Transaction updatedTransaction) {
        return transactionRepository.findByTransactionId(transactionId)
                .map(existingTransaction -> {
                    if (!validateTransactionPreExecution(updatedTransaction)) {
                        alertService.preExecutionAlert(transactionId, "Update validation failed.");
                        return null;
                    }
                    existingTransaction.setPrice(updatedTransaction.getPrice());
                    existingTransaction.setQuantity(updatedTransaction.getQuantity());
                    existingTransaction.setStatus(updatedTransaction.getStatus());
                    existingTransaction.setUid(updatedTransaction.getUid());
                    return transactionRepository.save(existingTransaction);
                })
                .orElse(null);
    }

    // Delete a transaction by ID
    public boolean deleteTransaction(String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findByTransactionId(transactionId);
        if (transaction.isPresent()) {
            transactionRepository.delete(transaction.get());
            alertService.preExecutionAlert(transactionId, "Transaction successfully deleted.");
            return true;
        }
        alertService.preExecutionAlert(transactionId, "Transaction deletion failed. Not found.");
        return false;
    }

    // Pre-execution validation for fat finger errors and range checks
    public boolean validateTransactionPreExecution(Transaction transaction) {
        boolean isValid = ValidationUtil.validateTransactionRange(transaction, minPrice, maxPrice, minQuantity, maxQuantity);

        if (!isValid) {
            alertService.preExecutionAlert(transaction.getTransactionId(), "Transaction failed range checks.");
        }

        return isValid;
    }

    // Track mismatches with an external data source
    public List<TransactionMismatch> trackMismatches(List<Transaction> externalTransactions) {
        List<TransactionMismatch> mismatches = new ArrayList<>();

        for (Transaction externalTransaction : externalTransactions) {
            Optional<Transaction> internalTransactionOpt = getTransactionById(externalTransaction.getTransactionId());

            if (internalTransactionOpt.isPresent()) {
                Transaction internalTransaction = internalTransactionOpt.get();
                if (!internalTransaction.equals(externalTransaction)) {
                    mismatches.add(createMismatch(internalTransaction, externalTransaction));
                }
            } else {
                mismatches.add(createMismatch(null, externalTransaction));
            }
        }

        return mismatches;
    }

    // Create a TransactionMismatch object for logging purposes
    private TransactionMismatch createMismatch(Transaction internalTransaction, Transaction externalTransaction) {
        TransactionMismatch mismatch = new TransactionMismatch();
        mismatch.setTransactionId(externalTransaction.getTransactionId());

        if (internalTransaction != null) {
            if (internalTransaction.getPrice() != externalTransaction.getPrice()) {
                mismatch.setField("Price");
                mismatch.setInternalValue(String.valueOf(internalTransaction.getPrice()));
                mismatch.setExternalValue(String.valueOf(externalTransaction.getPrice()));
            }
            if (internalTransaction.getQuantity() != externalTransaction.getQuantity()) {
                mismatch.setField("Quantity");
                mismatch.setInternalValue(String.valueOf(internalTransaction.getQuantity()));
                mismatch.setExternalValue(String.valueOf(externalTransaction.getQuantity()));
            }
        } else {
            mismatch.setField("Not Found");
            mismatch.setInternalValue("N/A");
            mismatch.setExternalValue(String.valueOf(externalTransaction));
        }

        return mismatch;
    }
}
