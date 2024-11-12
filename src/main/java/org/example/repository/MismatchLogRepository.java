package org.example.repository;

import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing MismatchLog data.
 * It provides methods for querying mismatches related to transactions.
 */
@Repository
public interface MismatchLogRepository extends JpaRepository<MismatchLog, Long> {

    /**
     * Find all mismatch logs associated with a specific transaction ID.
     *
     * @param transactionId the transaction ID to search for.
     * @return a list of mismatch logs related to the specified transaction ID.
     */
    List<MismatchLog> findByTransactionId(String transactionId);

    /**
     * Find transactions along with their associated mismatch logs by transaction ID.
     * This method uses a JOIN to fetch transaction and mismatch log data in a single query.
     *
     * @param transactionId the transaction ID to search for.
     * @return a list of Object arrays, where each array contains a Transaction and a MismatchLog.
     */
    @Query("SELECT t, m FROM Transaction t LEFT JOIN MismatchLog m ON t.transactionId = m.transactionId WHERE t.transactionId = :transactionId")
    List<Object[]> findTransactionWithMismatches(@Param("transactionId") String transactionId);
}
