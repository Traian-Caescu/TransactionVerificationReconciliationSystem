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

    @Query("SELECT s FROM StockData s WHERE " +
            "(:startDate IS NULL OR s.date >= :startDate) AND " +
            "(:endDate IS NULL OR s.date <= :endDate)")
    List<StockData> findByDateRange(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    @Query("SELECT s FROM StockData s WHERE " +
            "s.date BETWEEN :startDate AND :endDate " +
            "ORDER BY s.volume DESC")
    List<StockData> findByDateRangeSortedByVolume(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT s FROM StockData s WHERE " +
            "s.close > :closeThreshold AND " +
            "s.date BETWEEN :startDate AND :endDate")
    List<StockData> findHighClosingStocksInRange(@Param("closeThreshold") double closeThreshold,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    List<StockData> findByHighGreaterThan(double highThreshold);

    @Query("SELECT s FROM StockData s WHERE " +
            "s.high BETWEEN :lowBound AND :highBound AND " +
            "s.date BETWEEN :startDate AND :endDate")
    List<StockData> findStocksByHighPriceRangeAndDate(@Param("lowBound") double lowBound,
                                                      @Param("highBound") double highBound,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);
}
