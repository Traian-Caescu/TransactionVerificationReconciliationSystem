package org.example.dto;

import org.example.model.TransactionStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * Data Transfer Object for Transactions.
 * Holds details about a transaction, including price, quantity, UID, status, and symbol.
 */
public class TransactionDTO {

    @NotBlank(message = "Transaction ID is mandatory")
    private String transactionId;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be a positive value")
    private Double price;

    @NotNull(message = "Quantity is mandatory")
    @Positive(message = "Quantity must be a positive value")
    private Integer quantity;

    @NotBlank(message = "UID is mandatory")
    private String uid;

    @NotBlank(message = "Status is mandatory")
    @Size(max = 20, message = "Status cannot exceed 20 characters")
    private String status;

    private String assetClass; // Optional field for asset class categorization
    private String strategy;   // Optional field for strategy associated with the transaction
    private String symbol;     // Symbol associated with the transaction

    // Default constructor
    public TransactionDTO() {}

    /**
     * Parameterized constructor for TransactionDTO.
     *
     * @param transactionId the unique identifier for the transaction.
     * @param price         the price associated with the transaction.
     * @param quantity      the quantity associated with the transaction.
     * @param uid           the unique identifier for the transaction entity.
     * @param status        the status of the transaction, as an enum.
     * @param symbol        the symbol associated with the transaction.
     */
    public TransactionDTO(String transactionId, Double price, Integer quantity, String uid, TransactionStatus status, String symbol) {
        this.transactionId = transactionId;
        this.price = price;
        this.quantity = quantity;
        this.uid = uid;
        this.status = status != null ? status.name() : null;
        this.symbol = symbol;
    }

    // Getters and setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Converts the status field to the TransactionStatus enum.
     *
     * @return the status as a TransactionStatus enum.
     */
    public TransactionStatus getTransactionStatusEnum() {
        try {
            return TransactionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid transaction status: " + status);
        }
    }

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Provides a string representation of TransactionDTO for logging and debugging.
     *
     * @return formatted string representation of TransactionDTO instance.
     */
    @Override
    public String toString() {
        return "TransactionDTO{" +
                "transactionId='" + transactionId + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", uid='" + uid + '\'' +
                ", status='" + status + '\'' +
                ", assetClass='" + assetClass + '\'' +
                ", strategy='" + strategy + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
