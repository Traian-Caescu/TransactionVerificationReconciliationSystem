package org.example.service;

import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.example.repository.MismatchLogRepository;
import org.example.repository.TransactionRepository;
import org.example.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for handling transaction-related operations,
 * including saving, updating, deleting, and validating transactions.
 */
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

    /**
     * Saves a new transaction after validating it based on user role.
     *
     * @param transaction The transaction to be saved.
     * @return The saved transaction.
     */
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

    /**
     * Retrieves a transaction by its ID.
     *
     * @param transactionId The ID of the transaction.
     * @return The transaction if found, otherwise empty.
     */
    public Optional<Transaction> getTransactionById(String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        transaction.ifPresentOrElse(
                t -> alertService.preExecutionAlert(transactionId, "Transaction found."),
                () -> alertService.preExecutionAlert(transactionId, "Transaction not found.")
        );
        return transaction;
    }

    /**
     * Retrieves all transactions from the repository.
     *
     * @return A list of all transactions.
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Updates an existing transaction after validating it.
     *
     * @param transactionId         The ID of the transaction to be updated.
     * @param updatedTransaction    The updated transaction data.
     * @return The updated transaction if successful, otherwise null.
     */
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

    /**
     * Deletes a transaction by its ID.
     *
     * @param transactionId The ID of the transaction to be deleted.
     * @return true if the transaction was deleted, false if not found.
     */
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

    /**
     * Validates a transaction based on its price, quantity, and user role.
     *
     * @param transaction The transaction to be validated.
     * @param userRole    The role of the user performing the operation.
     * @return true if the transaction is valid, false otherwise.
     */
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

    /**
     * Logs rejected transactions in the mismatch table with a specific rejection reason.
     *
     * @param transaction The rejected transaction.
     * @param description A description of why the transaction was rejected.
     */
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

    /**
     * Retrieves the role of the currently authenticated user.
     *
     * @return The role of the authenticated user.
     */
    public String getUserRole() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getAuthorities().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("User has no roles"))
                    .getAuthority()
                    .replace("ROLE_", ""); // Remove "ROLE_" prefix
        }
        return "UNKNOWN";
    }

    /**
     * Pre-execution validation for a transaction. This method can be extended with more logic as required.
     *
     * @param transaction The transaction to be validated.
     * @return true for now as it assumes validation passes.
     */
    public boolean validateTransactionPreExecution(Transaction transaction) {
        return true; // Assume true for simplicity - logic long-term
    }
}
