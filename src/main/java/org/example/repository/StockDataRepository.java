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
            "(:endDate IS NULL OR s.date <= :endDate) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'open' THEN s.open " +
            "WHEN :sortBy = 'high' THEN s.high " +
            "WHEN :sortBy = 'low' THEN s.low " +
            "WHEN :sortBy = 'close' THEN s.close " +
            "WHEN :sortBy = 'volume' THEN s.volume " +
            "ELSE s.date END " +
            "ASC")
    List<StockData> findByDateRangeAndSort(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           @Param("sortBy") String sortBy,
                                           @Param("asc") boolean asc);
}
