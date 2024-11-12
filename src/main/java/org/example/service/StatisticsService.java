package org.example.service;

import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.example.repository.MismatchLogRepository;
import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    // Overall statistics
    public Map<String, Long> getTransactionStatistics() {
        List<Transaction> transactions = transactionRepository.findAll();

        return transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getStatus().toString(),
                        Collectors.counting()
                ));
    }

    // Filtered statistics for SENIOR role
    public Map<String, Long> getSeniorStatistics() {
        List<Transaction> transactions = transactionRepository.findByStatus(TransactionStatus.PENDING);

        return transactions.stream()
                .filter(transaction -> transaction.getStatus() == TransactionStatus.PENDING)
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getStatus().toString(),
                        Collectors.counting()
                ));
    }

    // Mismatch statistics
    public long getTotalMismatches() {
        return mismatchLogRepository.count();
    }

    // Mismatch statistics grouped by field
    public Map<String, Long> getMismatchStatisticsByField() {
        List<MismatchLog> mismatches = mismatchLogRepository.findAll();

        return mismatches.stream()
                .collect(Collectors.groupingBy(MismatchLog::getField, Collectors.counting()));
    }
}
