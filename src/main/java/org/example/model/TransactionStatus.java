package org.example.model;

/**
 * Enum representing the different statuses of a transaction.
 * These statuses are used to track the state of a transaction throughout its lifecycle.
 */
public enum TransactionStatus {

    /** Indicates that the transaction is pending and not yet processed. */
    PENDING,

    /** Indicates that the transaction has been rejected due to failure in validation or other criteria. */
    REJECTED,

    /** Indicates that the transaction has been completed successfully. */
    COMPLETED;

    /**
     * Get a string representation of the status.
     *
     * @return the string value of the status.
     */
    @Override
    public String toString() {
        return name();
    }
}
