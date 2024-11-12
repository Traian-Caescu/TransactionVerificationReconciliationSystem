package org.example.repository;

import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByStatus(TransactionStatus status);

    @Query("SELECT t.transactionId, t.uid, t.price, t.quantity, t.status, COUNT(m) as mismatchCount " +
            "FROM Transaction t LEFT JOIN MismatchLog m ON t.transactionId = m.transactionId " +
            "GROUP BY t.transactionId")
    List<Object[]> findAllTransactionsWithMismatchCount();
}
