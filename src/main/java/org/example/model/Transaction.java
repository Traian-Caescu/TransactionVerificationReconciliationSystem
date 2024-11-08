package org.example.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Transaction {
    @Id
    private String transactionId;
    private String uid; // Unique identifier for the trader
    private double price;
    private int quantity;
    private String status; // Status of the transaction (e.g., executed, pending)

    // Constructors
    public Transaction() {}

    public Transaction(String transactionId, String uid, double price, int quantity, String status) {
        this.transactionId = transactionId;
        this.uid = uid;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
    }

    public Transaction(String transactionId, Double price, Integer quantity, String uid, String status) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
