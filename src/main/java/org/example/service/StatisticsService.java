package org.example.service;

import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.example.repository.MismatchLogRepository;
import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final TransactionRepository transactionRepository;
    private final MismatchLogRepository mismatchLogRepository;

    @Autowired
    public StatisticsService(TransactionRepository transactionRepository, MismatchLogRepository mismatchLogRepository) {
        this.transactionRepository = transactionRepository;
        this.mismatchLogRepository = mismatchLogRepository;
    }

    // Existing statistics methods
    public Map<String, Long> getTransactionStatistics() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getStatus().toString(),
                        Collectors.counting()
                ));
    }

    public Map<String, Long> getSeniorStatistics() {
        List<Transaction> transactions = transactionRepository.findByStatus(TransactionStatus.PENDING);
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getStatus().toString(),
                        Collectors.counting()
                ));
    }

    public long getTotalMismatches() {
        return mismatchLogRepository.count();
    }

    public Map<String, Long> getMismatchStatisticsByField() {
        List<MismatchLog> mismatches = mismatchLogRepository.findAll();
        return mismatches.stream()
                .collect(Collectors.groupingBy(MismatchLog::getField, Collectors.counting()));
    }

    // New methods for join-based data retrieval

    // Fetch transactions with associated mismatches by transaction ID
    public Map<String, Object> getTransactionWithMismatches(String transactionId) {
        List<Object[]> results = mismatchLogRepository.findTransactionWithMismatches(transactionId);
        Map<String, Object> transactionData = new HashMap<>();

        for (Object[] row : results) {
            Transaction transaction = (Transaction) row[0];
            MismatchLog mismatchLog = (MismatchLog) row[1];

            transactionData.put("transactionId", transaction.getTransactionId());
            transactionData.put("uid", transaction.getUid());
            transactionData.put("price", transaction.getPrice());
            transactionData.put("quantity", transaction.getQuantity());
            transactionData.put("status", transaction.getStatus());

            if (mismatchLog != null) {
                transactionData.put("mismatchField", mismatchLog.getField());
                transactionData.put("internalValue", mismatchLog.getInternalValue());
                transactionData.put("externalValue", mismatchLog.getExternalValue());
                transactionData.put("source", mismatchLog.getSource());
                transactionData.put("description", mismatchLog.getDescription());
            }
        }
        return transactionData;
    }

    // Fetch all transactions with mismatch counts
    public List<Map<String, Object>> getAllTransactionsWithMismatchCounts() {
        List<Object[]> results = transactionRepository.findAllTransactionsWithMismatchCount();
        List<Map<String, Object>> transactionsWithCounts = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> transactionData = new HashMap<>();
            transactionData.put("transactionId", row[0]);
            transactionData.put("uid", row[1]);
            transactionData.put("price", row[2]);
            transactionData.put("quantity", row[3]);
            transactionData.put("status", row[4]);
            transactionData.put("mismatchCount", row[5]);

            transactionsWithCounts.add(transactionData);
        }

        return transactionsWithCounts;
    }
}
