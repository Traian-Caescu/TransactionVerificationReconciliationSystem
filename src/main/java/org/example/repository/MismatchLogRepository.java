package org.example.repository;

import org.example.model.MismatchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MismatchLogRepository extends JpaRepository<MismatchLog, Long> {

    // Custom method to find mismatches by transaction ID, useful for transaction-specific alerts and reporting
    List<MismatchLog> findByTransactionId(String transactionId);

    // Custom method to find mismatches by source, aiding in identifying issues specific to external sources
    List<MismatchLog> findBySource(String source);

    // Custom method to retrieve mismatches by specific field (e.g., price, quantity) to focus on targeted discrepancies
    List<MismatchLog> findByField(String field);

    // Custom method to retrieve all mismatches by transaction ID and field, useful for detailed comparison reports
    List<MismatchLog> findByTransactionIdAndField(String transactionId, String field);
}