package org.example.repository;

import org.example.model.MismatchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MismatchLogRepository extends JpaRepository<MismatchLog, Long> {

    // Custom method to find mismatches by transaction ID
    List<MismatchLog> findByTransactionId(String transactionId);
}
