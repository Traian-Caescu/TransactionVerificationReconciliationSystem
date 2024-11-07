package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "mismatch_logs")
public class MismatchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;
    private String field;  // The field where the mismatch occurred (e.g., "price", "quantity")
    private String internalValue;  // The value in our system
    private String externalValue;  // The value from the external source

    // Constructors, Getters, and Setters

    public MismatchLog() {}

    public MismatchLog(String transactionId, String field, String internalValue, String externalValue) {
        this.transactionId = transactionId;
        this.field = field;
        this.internalValue = internalValue;
        this.externalValue = externalValue;
    }

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
}
