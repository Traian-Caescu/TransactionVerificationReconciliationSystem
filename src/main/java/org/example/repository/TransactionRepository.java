package org.example.repository;

import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByUid(String uid);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByPriceBetween(double minPrice, double maxPrice);

    List<Transaction> findByQuantityBetween(int minQuantity, int maxQuantity);

    Optional<Transaction> findBySymbol(String symbol);

    List<Transaction> findByUidAndStatus(String uid, TransactionStatus status);
}
