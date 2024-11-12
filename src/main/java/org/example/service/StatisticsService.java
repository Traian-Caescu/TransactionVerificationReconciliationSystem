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

/**
 * Service class for handling transaction and mismatch statistics.
 */
@Service
public class StatisticsService {

    private final TransactionRepository transactionRepository;
    private final MismatchLogRepository mismatchLogRepository;

    @Autowired
    public StatisticsService(TransactionRepository transactionRepository, MismatchLogRepository mismatchLogRepository) {
        this.transactionRepository = transactionRepository;
        this.mismatchLogRepository = mismatchLogRepository;
    }

    /**
     * Retrieves transaction statistics by status.
     *
     * @return A map with the status of transactions as keys and the count of each status as values.
     */
    public Map<String, Long> getTransactionStatistics() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getStatus().toString(),
                        Collectors.counting()
                ));
    }

    /**
     * Retrieves statistics related to senior transactions.
     *
     * @return A map with the status of transactions for senior users as keys and the count of each status as values.
     */
    public Map<String, Long> getSeniorStatistics() {
        List<Transaction> transactions = transactionRepository.findByStatus(TransactionStatus.PENDING);
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getStatus().toString(),
                        Collectors.counting()
                ));
    }

    /**
     * Retrieves the total number of mismatches recorded in the system.
     *
     * @return The total number of mismatches.
     */
    public long getTotalMismatches() {
        return mismatchLogRepository.count();
    }

    /**
     * Retrieves statistics about mismatches by field.
     *
     * @return A map with the field name as the key and the count of mismatches for that field as the value.
     */
    public Map<String, Long> getMismatchStatisticsByField() {
        List<MismatchLog> mismatches = mismatchLogRepository.findAll();
        return mismatches.stream()
                .collect(Collectors.groupingBy(MismatchLog::getField, Collectors.counting()));
    }

    /**
     * Retrieves detailed information about a transaction, including any mismatches, based on the transaction ID.
     *
     * @param transactionId The ID of the transaction.
     * @return A map containing transaction details and any associated mismatches.
     */
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

    /**
     * Retrieves a list of all transactions along with their mismatch counts.
     *
     * @return A list of maps containing transaction details and the associated mismatch count.
     */
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
