package org.example.controller;

import org.example.dto.TransactionDTO;
import org.example.model.Transaction;
import org.example.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

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

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String transactionId) {
        return transactionService.getTransactionById(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<TransactionDTO> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return transactions.stream().map(transaction -> new TransactionDTO(
                transaction.getTransactionId(),
                transaction.getPrice(),
                transaction.getQuantity(),
                transaction.getUid(),
                transaction.getStatus()
        )).collect(Collectors.toList());
    }
}
