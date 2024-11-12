package org.example.repository;

import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MismatchLogRepository extends JpaRepository<MismatchLog, Long> {

    List<MismatchLog> findByTransactionId(String transactionId);

    @Query("SELECT t, m FROM Transaction t LEFT JOIN MismatchLog m ON t.transactionId = m.transactionId WHERE t.transactionId = :transactionId")
    List<Object[]> findTransactionWithMismatches(@Param("transactionId") String transactionId);
}
