package org.example.repository;

import org.example.model.MismatchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MismatchLogRepository extends JpaRepository<MismatchLog, Long> {

    List<MismatchLog> findByTransactionId(String transactionId);

    List<MismatchLog> findBySource(String source);

    List<MismatchLog> findByField(String field);

    List<MismatchLog> findByTransactionIdAndField(String transactionId, String field);

    List<MismatchLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<MismatchLog> findByDescriptionContaining(String description);

    @Query("SELECT m FROM MismatchLog m WHERE m.source = :source AND m.timestamp BETWEEN :start AND :end")
    List<MismatchLog> findBySourceAndTimestampBetween(@Param("source") String source,
                                                      @Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end);

    @Query("SELECT m FROM MismatchLog m WHERE m.field = :field AND m.source = :source")
    List<MismatchLog> findByFieldAndSource(@Param("field") String field, @Param("source") String source);

    @Query("SELECT m FROM MismatchLog m WHERE m.field = :field AND m.source = :source AND m.timestamp BETWEEN :start AND :end")
    List<MismatchLog> findByFieldSourceAndTimestampBetween(@Param("field") String field,
                                                           @Param("source") String source,
                                                           @Param("start") LocalDateTime start,
                                                           @Param("end") LocalDateTime end);
}
