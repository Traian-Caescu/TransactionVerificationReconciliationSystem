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

    // Thresholds for validation
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

    // Save a new transaction with pre-execution validation
    public Transaction saveTransaction(Transaction transaction) {
        boolean isValid = validateTransactionPreExecution(transaction);

        if (isValid) {
            return transactionRepository.save(transaction);
        } else {
            alertService.preExecutionAlert(transaction.getTransactionId(), "Transaction failed validation checks.");
            return null;
        }
    }

    // Retrieve a transaction by ID
    public Optional<Transaction> getTransactionById(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId);
    }

    // Retrieve all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Update an existing transaction
    public Transaction updateTransaction(String transactionId, Transaction updatedTransaction) {
        return transactionRepository.findByTransactionId(transactionId)
                .map(existingTransaction -> {
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
            return true;
        }
        return false;
    }

    // Pre-execution validation for fat finger errors and range checks
    private boolean validateTransactionPreExecution(Transaction transaction) {
        boolean isValid = ValidationUtil.validateTransactionRange(transaction, minPrice, maxPrice, minQuantity, maxQuantity);

        if (!isValid) {
            System.out.println("Alert: Potential input error for transaction ID " + transaction.getTransactionId() +
                    ". Check price and quantity ranges before execution.");
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
                if (!internalTransaction.equals(externalTransaction)) { // Ensure equals method is implemented
                    mismatches.add(createMismatch(internalTransaction, externalTransaction));
                }
            } else {
                mismatches.add(createMismatch(null, externalTransaction)); // Transaction not found in internal records
            }
        }

        return mismatches;
    }

    // Create a TransactionMismatch object
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
            // Add checks for other fields as needed
        } else {
            mismatch.setField("Not Found");
            mismatch.setInternalValue("N/A");
            mismatch.setExternalValue(String.valueOf(externalTransaction));
        }

        return mismatch;
    }
}
