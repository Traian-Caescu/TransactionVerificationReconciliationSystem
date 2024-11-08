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

    // Update transaction
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

    // Delete a transaction
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
}
