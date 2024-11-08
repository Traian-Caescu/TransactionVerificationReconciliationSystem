package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "mismatch_logs")
public class MismatchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String field;  // The field where the mismatch occurred (e.g., "price", "quantity")

    @Column(nullable = false)
    private String internalValue;  // The value in our system

    @Column(nullable = false)
    private String externalValue;  // The value from the external source

    @Column(nullable = true)
    private String source;  // Source of the external data

    // Default constructor
    public MismatchLog() {}

    // Parameterized constructor
    public MismatchLog(String transactionId, String field, String internalValue, String externalValue, String source) {
        this.transactionId = transactionId;
        this.field = field;
        this.internalValue = internalValue;
        this.externalValue = externalValue;
        this.source = source;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getField() {
        return field;
    }

    public String getInternalValue() {
        return internalValue;
    }

    public String getExternalValue() {
        return externalValue;
    }

    public String getSource() {
        return source;
    }

    // Setters
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setInternalValue(String internalValue) {
        this.internalValue = internalValue;
    }

    public void setExternalValue(String externalValue) {
        this.externalValue = externalValue;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
