package org.example.repository; 

import org.example.model.Transaction;
import org.example.model.TransactionStatus; 
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.data.jpa.repository.Query; 
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing Transaction data.
 * It provides methods for querying transactions and their status.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    /**
     * Find all transactions with a specific status.
     *
     * @param status the status of the transactions to search for.
     * @return a list of transactions with the specified status.
     */
    List<Transaction> findByStatus(TransactionStatus status);

    /**
     * Find all transactions along with their associated mismatch count.
     * This query performs a LEFT JOIN with the MismatchLog table to count mismatches per transaction.
     *
     * @return a list of Object arrays, where each array contains the transaction data and its mismatch count.
     */
    @Query("SELECT t.transactionId, t.uid, t.price, t.quantity, t.status, COUNT(m) as mismatchCount " +
            "FROM Transaction t LEFT JOIN MismatchLog m ON t.transactionId = m.transactionId " +
            "GROUP BY t.transactionId")
    List<Object[]> findAllTransactionsWithMismatchCount();
}
