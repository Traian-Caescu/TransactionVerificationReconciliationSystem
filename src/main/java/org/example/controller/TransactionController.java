package org.example.controller;

import org.example.dto.TransactionDTO;
import org.example.model.Transaction;
import org.example.service.TransactionService;
import org.example.service.AlertService;
import org.example.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing transactions in the system.
 * Provides CRUD operations and validation for transactions.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AlertService alertService;
    private final VerificationService verificationService;

    /**
     * Constructor to initialize required services.
     *
     * @param transactionService service for transaction management.
     * @param alertService       service for alerting and mismatch notifications.
     * @param verificationService service for transaction verification.
     */
    @Autowired
    public TransactionController(TransactionService transactionService, AlertService alertService, VerificationService verificationService) {
        this.transactionService = transactionService;
        this.alertService = alertService;
        this.verificationService = verificationService;
    }

    /**
     * Creates a new transaction with validation checks.
     *
     * @param transactionDTO DTO containing transaction data.
     * @return response entity with saved transaction or an error message.
     */
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction(
                transactionDTO.getTransactionId(),
                transactionDTO.getUid(),
                transactionDTO.getPrice(),
                transactionDTO.getQuantity(),
                transactionDTO.getTransactionStatusEnum(),
                transactionDTO.getSymbol()
        );

        if (transactionService.validateTransactionPreExecution(transaction)) {
            Transaction savedTransaction = transactionService.saveTransaction(transaction);
            return ResponseEntity.ok(savedTransaction);
        } else {
            alertService.preExecutionAlert(transaction.getTransactionId(), "Transaction failed pre-execution validation.");
            return ResponseEntity.badRequest().body("Transaction validation failed.");
        }
    }

    /**
     * Retrieves a transaction by ID, with alerting if not found.
     *
     * @param transactionId ID of the transaction to retrieve.
     * @return response entity with the transaction or a not found status.
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String transactionId) {
        return transactionService.getTransactionById(transactionId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    alertService.preExecutionAlert(transactionId, "Transaction not found.");
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Retrieves all transactions in the system.
     *
     * @return response entity with a list of transaction DTOs.
     */
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transaction -> new TransactionDTO(
                        transaction.getTransactionId(),
                        transaction.getPrice(),
                        transaction.getQuantity(),
                        transaction.getUid(),
                        transaction.getStatus(),
                        transaction.getSymbol()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }

    /**
     * Updates an existing transaction with validation.
     *
     * @param transactionId ID of the transaction to update.
     * @param transactionDTO DTO containing updated transaction data.
     * @return response entity with updated transaction or an error message.
     */
    @PutMapping("/{transactionId}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable String transactionId,
            @RequestBody TransactionDTO transactionDTO) {

        Transaction updatedTransaction = new Transaction(
                transactionId,
                transactionDTO.getUid(),
                transactionDTO.getPrice(),
                transactionDTO.getQuantity(),
                transactionDTO.getTransactionStatusEnum(),
                transactionDTO.getSymbol()
        );

        if (!transactionService.validateTransactionPreExecution(updatedTransaction)) {
            alertService.preExecutionAlert(transactionId, "Update failed validation checks.");
            return ResponseEntity.badRequest().body("Transaction update validation failed.");
        }

        Transaction result = transactionService.updateTransaction(transactionId, updatedTransaction);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    /**
     * Deletes a transaction by ID, with alerting if not found.
     *
     * @param transactionId ID of the transaction to delete.
     * @return response entity with no content if deleted, or not found status.
     */
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<?> deleteTransaction(@PathVariable String transactionId) {
        boolean isDeleted = transactionService.deleteTransaction(transactionId);
        if (isDeleted) {
            alertService.preExecutionAlert(transactionId, "Transaction successfully deleted.");
            return ResponseEntity.noContent().build();
        } else {
            alertService.preExecutionAlert(transactionId, "Transaction deletion failed. Not found.");
            return ResponseEntity.notFound().build();
        }
    }
}
