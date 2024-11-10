package org.example.repository;

import org.example.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // Custom method to find transactions by UID, useful for user-specific transaction tracking
    List<Transaction> findByUid(String uid);

    // Custom method to find transactions by status (e.g., pending, executed)
    List<Transaction> findByStatus(String status);

    // Custom method to retrieve transactions within a specified price range, enhancing filtering options
    List<Transaction> findByPriceBetween(double minPrice, double maxPrice);

    // Custom method to retrieve transactions within a specified quantity range
    List<Transaction> findByQuantityBetween(int minQuantity, int maxQuantity);

    Optional<Transaction> findByTransactionId(String transactionId);

}