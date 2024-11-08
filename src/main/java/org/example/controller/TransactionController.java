package org.example.controller;

import org.example.dto.TransactionDTO;
import org.example.model.Transaction;
import org.example.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Endpoint to create a new transaction
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction(
                transactionDTO.getTransactionId(),
                transactionDTO.getPrice(),
                transactionDTO.getQuantity(),
                transactionDTO.getUid(),
                transactionDTO.getStatus()
        );
        Transaction savedTransaction = transactionService.saveTransaction(transaction);
        return ResponseEntity.ok(savedTransaction);
    }

    // Endpoint to retrieve a transaction by ID
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String transactionId) {
        return transactionService.getTransactionById(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint to retrieve all transactions
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        List<TransactionDTO> transactionDTOs = transactions.stream().map(transaction -> new TransactionDTO(
                transaction.getTransactionId(),
                transaction.getPrice(),
                transaction.getQuantity(),
                transaction.getUid(),
                transaction.getStatus()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }

    // Endpoint to update an existing transaction by ID
    @PutMapping("/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable String transactionId,
            @RequestBody TransactionDTO transactionDTO) {
        Transaction updatedTransaction = new Transaction(
                transactionDTO.getTransactionId(),
                transactionDTO.getPrice(),
                transactionDTO.getQuantity(),
                transactionDTO.getUid(),
                transactionDTO.getStatus()
        );
        Transaction result = transactionService.updateTransaction(transactionId, updatedTransaction);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    // Endpoint to delete a transaction by ID
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String transactionId) {
        boolean isDeleted = transactionService.deleteTransaction(transactionId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
