package org.example.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;  // Description of the action (e.g., "Transaction Verified", "Mismatch Detected")

    @Column(nullable = false)
    private String details;  // Detailed information about the action

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Default constructor
    public AuditLog() {
        this.timestamp = LocalDateTime.now(); // Set timestamp to now by default
    }

    // Parameterized constructor
    public AuditLog(String action, String details) {
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setAction(String action) {
        this.action = action;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
