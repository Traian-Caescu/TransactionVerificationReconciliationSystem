package org.example.model;

/**
 * Data Transfer Object (DTO) that represents a mismatch between a transaction and its external data.
 * This class is used to encapsulate mismatch details between the internal transaction and the external data source.
 */
public class TransactionMismatch {

    private String transactionId;
    private String field;
    private String internalValue;
    private String externalValue;

    /**
     * Default constructor.
     */
    public TransactionMismatch() {}

    /**
     * Parameterized constructor for creating a new TransactionMismatch object.
     *
     * @param transactionId the unique transaction ID.
     * @param field         the field that has a mismatch.
     * @param internalValue the value from the internal transaction.
     * @param externalValue the value from the external data.
     */
    public TransactionMismatch(String transactionId, String field, String internalValue, String externalValue) {
        this.transactionId = transactionId;
        this.field = field;
        this.internalValue = internalValue;
        this.externalValue = externalValue;
    }

    // Getters and Setters
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

    /**
     * Provides a string representation of the mismatch for logging and debugging purposes.
     *
     * @return a formatted string representation of the mismatch.
     */
    @Override
    public String toString() {
        return "TransactionMismatch{" +
                "transactionId='" + transactionId + '\'' +
                ", field='" + field + '\'' +
                ", internalValue='" + internalValue + '\'' +
                ", externalValue='" + externalValue + '\'' +
                '}';
    }
}
