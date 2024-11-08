package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId; // Unique identifier for each transaction

    @Column(nullable = false)
    private double price; // Price of the transaction

    @Column(nullable = false)
    private int quantity; // Quantity involved in the transaction

    @Column(nullable = false)
    private String uid; // UID of the trader

    private String status; // Status of the transaction (e.g., completed, pending)

    // Default constructor
    public Transaction() {}

    // Parameterized constructor
    public Transaction(String transactionId, double price, int quantity, String uid, String status) {
        this.transactionId = transactionId;
        this.price = price;
        this.quantity = quantity;
        this.uid = uid;
        this.status = status;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUid() {
        return uid;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
