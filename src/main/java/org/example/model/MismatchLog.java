package org.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity representing a log entry for mismatches in transactions.
 * Stores details about the mismatch, including transaction ID, field, source, and timestamps.
 */
@Entity
@Table(name = "mismatch_logs")
public class MismatchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotBlank(message = "Field is required")
    private String field;

    private String internalValue;
    private String externalValue;

    @NotBlank(message = "Source is required")
    private String source;

    private String description;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * Default constructor initializing the timestamp to the current time.
     */
    public MismatchLog() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Parameterized constructor to initialize a mismatch log entry.
     *
     * @param transactionId the ID of the transaction related to the mismatch.
     * @param field         the field in which the mismatch occurred.
     * @param internalValue the expected internal value.
     * @param externalValue the actual external value found.
     * @param source        the source of the mismatch.
     * @param description   a description of the mismatch.
     */
    public MismatchLog(String transactionId, String field, String internalValue, String externalValue, String source, String description) {
        this.transactionId = transactionId;
        this.field = field;
        this.internalValue = internalValue;
        this.externalValue = externalValue;
        this.source = source;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getInternalValue() {
        return internalValue;
    }

    public void setInternalValue(String internalValue) {
        this.internalValue = internalValue;
    }

    public String getExternalValue() {
        return externalValue;
    }

    public void setExternalValue(String externalValue) {
        this.externalValue = externalValue;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Provides a string representation of the mismatch log entry for logging and debugging.
     *
     * @return formatted string representation of the mismatch log entry.
     */
    @Override
    public String toString() {
        return "MismatchLog{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", field='" + field + '\'' +
                ", internalValue='" + internalValue + '\'' +
                ", externalValue='" + externalValue + '\'' +
                ", source='" + source + '\'' +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
