package org.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Action description is required")
    private String action;  // Description of the action (e.g., "Transaction Verified", "Mismatch Detected")

    @NotBlank(message = "Details are required")
    @Column(length = 500) // Set a max length for the details field
    private String details;  // Detailed information about the action

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    private String performedBy; // Name or ID of the user who performed the action

    private String ipAddress; // IP address from where the action was performed, for security tracking

    // Default constructor
    public AuditLog() {
        this.timestamp = LocalDateTime.now(); // Automatically set timestamp on creation
    }

    // Parameterized constructor
    public AuditLog(String action, String details, String performedBy, String ipAddress) {
        this.action = action;
        this.details = details;
        this.performedBy = performedBy;
        this.ipAddress = ipAddress;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}