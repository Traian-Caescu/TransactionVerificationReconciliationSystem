package org.example.repository;

import org.example.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Custom method to find audit logs by specific action type (e.g., "Transaction Verified")
    List<AuditLog> findByAction(String action);

    // Custom method to find audit logs within a specific time range for date-based filtering
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Custom method to retrieve audit logs containing specific keywords in details, useful for in-depth search
    List<AuditLog> findByDetailsContaining(String keyword);
}