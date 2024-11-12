package org.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Entity representing a transaction.
 * Stores details such as transaction ID, UID, price, quantity, status, and related information.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @Column(name = "uid", nullable = false, unique = true)
    private String uid;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private double price;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private int quantity;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String assetClass;
    private String strategy;
    private String symbol;

    /**
     * Default constructor.
     */
    public Transaction() {}

    /**
     * Parameterized constructor for creating a new transaction.
     *
     * @param transactionId the unique transaction ID.
     * @param uid           the user ID associated with the transaction.
     * @param price         the price of the transaction.
     * @param quantity      the quantity of the transaction.
     * @param status        the current status of the transaction.
     * @param symbol        the symbol of the asset involved in the transaction.
     */
    public Transaction(String transactionId, String uid, double price, int quantity, TransactionStatus status, String symbol) {
        this.transactionId = transactionId;
        this.uid = uid;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.symbol = symbol;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
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
     * Provides a string representation of the transaction for logging and debugging purposes.
     *
     * @return a formatted string representation of the transaction.
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", uid='" + uid + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", status=" + status +
                ", assetClass='" + assetClass + '\'' +
                ", strategy='" + strategy + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
