package org.example.service;

import org.example.model.Transaction;
import org.example.repository.TransactionRepository;
import org.example.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    // Save a new transaction with validation
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

    // Update an existing transaction
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
        return ValidationUtil.validateTransactionRange(transaction, minPrice, maxPrice, minQuantity, maxQuantity);
    }
}
