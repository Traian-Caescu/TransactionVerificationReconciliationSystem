package org.example.repository;

import org.example.entity.StockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockDataRepository extends JpaRepository<StockData, Long> {

    // Custom method to find stock data by date range
    @Query("SELECT s FROM StockData s WHERE " +
            "(:startDate IS NULL OR s.date >= :startDate) AND " +
            "(:endDate IS NULL OR s.date <= :endDate)")
    List<StockData> findByDateRange(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

}