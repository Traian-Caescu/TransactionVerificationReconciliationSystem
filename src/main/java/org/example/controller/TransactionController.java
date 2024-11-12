package org.example.controller;

import org.example.dto.TransactionDTO;
import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.example.service.TransactionService;
import org.example.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AlertService alertService;

    @Autowired
    public TransactionController(TransactionService transactionService, AlertService alertService) {
        this.transactionService = transactionService;
        this.alertService = alertService;
    }


    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction(
                transactionDTO.getTransactionId(),
                transactionDTO.getUid(),
                transactionDTO.getPrice(),
                transactionDTO.getQuantity(),
                transactionDTO.getTransactionStatusEnum(), // Convert String to enum
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

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String transactionId) {
        return transactionService.getTransactionById(transactionId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    alertService.preExecutionAlert(transactionId, "Transaction not found.");
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transaction -> new TransactionDTO(
                        transaction.getTransactionId(),
                        transaction.getPrice(),
                        transaction.getQuantity(),
                        transaction.getUid(),
                        transaction.getStatus(), // Enum passed to DTO constructor
                        transaction.getSymbol()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable String transactionId,
            @RequestBody TransactionDTO transactionDTO) {

        Transaction updatedTransaction = new Transaction(
                transactionId,
                transactionDTO.getUid(),
                transactionDTO.getPrice(),
                transactionDTO.getQuantity(),
                transactionDTO.getTransactionStatusEnum(), // Convert String to enum
                transactionDTO.getSymbol()
        );

        if (!transactionService.validateTransactionPreExecution(updatedTransaction)) {
            alertService.preExecutionAlert(transactionId, "Update failed validation checks.");
            return ResponseEntity.badRequest().body("Transaction update validation failed.");
        }

        Transaction result = transactionService.updateTransaction(transactionId, updatedTransaction);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

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
