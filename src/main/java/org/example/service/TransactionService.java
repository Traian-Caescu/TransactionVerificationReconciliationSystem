package org.example.service;

import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.example.repository.TransactionRepository;
import org.example.repository.MismatchLogRepository;
import org.example.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final MismatchLogRepository mismatchLogRepository;
    private final AlertService alertService;

    @Value("${transaction.price.min:10}")
    private double minPrice;

    @Value("${transaction.price.max:10000}")
    private double maxPrice;

    @Value("${transaction.quantity.min:1}")
    private int minQuantity;

    @Value("${transaction.quantity.max:1000}")
    private int maxQuantity;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, MismatchLogRepository mismatchLogRepository, AlertService alertService) {
        this.transactionRepository = transactionRepository;
        this.mismatchLogRepository = mismatchLogRepository;
        this.alertService = alertService;
    }


    // Save a new transaction with role-specific validation, alerting, and status setting
    public Transaction saveTransaction(Transaction transaction) {
        String userRole = getUserRole();
        boolean isValid = validateTransaction(transaction, userRole);

        if (!isValid) {
            logRejectedTransaction(transaction, "Transaction exceeds limits for " + userRole + " role.");
            alertService.preExecutionAlert(transaction.getTransactionId(), "Transaction validation failed for " + userRole);
            transaction.setStatus(TransactionStatus.REJECTED);  // Use TransactionStatus enum directly
            return transactionRepository.save(transaction);  // Save as rejected for tracking
        }

        transaction.setStatus(TransactionStatus.PENDING);  // Default to pending status for new transactions
        return transactionRepository.save(transaction);
    }

    // Retrieve a transaction by ID with optional alerting
    public Optional<Transaction> getTransactionById(String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        transaction.ifPresentOrElse(
                t -> alertService.preExecutionAlert(transactionId, "Transaction found."),
                () -> alertService.preExecutionAlert(transactionId, "Transaction not found.")
        );
        return transaction;
    }

    // Retrieve all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Update an existing transaction with role-specific validation, alerting, and status updating
    public Transaction updateTransaction(String transactionId, Transaction updatedTransaction) {
        String userRole = getUserRole();
        boolean isValid = validateTransaction(updatedTransaction, userRole);

        if (!isValid) {
            logRejectedTransaction(updatedTransaction, "Update failed validation for " + userRole + " role.");
            alertService.preExecutionAlert(transactionId, "Update validation failed.");
            updatedTransaction.setStatus(TransactionStatus.REJECTED);  // Use TransactionStatus enum directly
            return transactionRepository.save(updatedTransaction);
        }

        return transactionRepository.findById(transactionId)
                .map(existingTransaction -> {
                    existingTransaction.setPrice(updatedTransaction.getPrice());
                    existingTransaction.setQuantity(updatedTransaction.getQuantity());
                    existingTransaction.setStatus(updatedTransaction.getStatus());
                    existingTransaction.setUid(updatedTransaction.getUid());
                    return transactionRepository.save(existingTransaction);
                })
                .orElse(null);
    }

    // Delete a transaction by ID with alerting
    public boolean deleteTransaction(String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        if (transaction.isPresent()) {
            transactionRepository.delete(transaction.get());
            alertService.preExecutionAlert(transactionId, "Transaction successfully deleted.");
            return true;
        }
        alertService.preExecutionAlert(transactionId, "Transaction deletion failed. Not found.");
        return false;
    }

    // Role-based validation for transactions
    private boolean validateTransaction(Transaction transaction, String userRole) {
        if ("SENIOR".equals(userRole)) {
            return ValidationUtil.validateTransactionRange(transaction, minPrice, maxPrice, minQuantity, maxQuantity);
        } else if ("JUNIOR".equals(userRole)) {
            double juniorMaxPrice = maxPrice * 0.5;
            int juniorMaxQuantity = maxQuantity / 2;
            return ValidationUtil.validateTransactionRange(transaction, minPrice, juniorMaxPrice, minQuantity, juniorMaxQuantity);
        }
        return false;
    }

    // Log rejected transactions in the mismatch table with specific rejection reason
    private void logRejectedTransaction(Transaction transaction, String description) {
        MismatchLog mismatchLog = new MismatchLog(
                transaction.getTransactionId(),
                "transaction",
                "Rejected",
                "Exceeded limits",
                "System",
                description
        );
        mismatchLogRepository.save(mismatchLog);
    }

    // Helper method to retrieve the role of the currently authenticated user
    public String getUserRole() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getAuthorities().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("User has no roles"))
                    .getAuthority()
                    .replace("ROLE_", "");
        }
        return "UNKNOWN";
    }

    // Pre-execution validation stub
    public boolean validateTransactionPreExecution(Transaction transaction) {
        return true; // Assume true for simplicity - logic long-term
    }
}
