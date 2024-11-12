package org.example.repository;

import org.example.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByAction(String action);

    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<AuditLog> findByDetailsContaining(String keyword);

    List<AuditLog> findByPerformedBy(String performedBy);

    List<AuditLog> findByIpAddress(String ipAddress);

    @Query("SELECT a FROM AuditLog a WHERE a.action = :action AND a.performedBy = :performedBy")
    List<AuditLog> findByActionAndUser(@Param("action") String action, @Param("performedBy") String performedBy);

    @Query("SELECT a FROM AuditLog a WHERE a.action = :action AND a.timestamp BETWEEN :start AND :end")
    List<AuditLog> findByActionAndTimestampBetween(@Param("action") String action,
                                                   @Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end);

    @Query("SELECT a FROM AuditLog a WHERE a.ipAddress = :ipAddress AND a.timestamp BETWEEN :start AND :end")
    List<AuditLog> findByIpAddressAndTimestampBetween(@Param("ipAddress") String ipAddress,
                                                      @Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end);
}
