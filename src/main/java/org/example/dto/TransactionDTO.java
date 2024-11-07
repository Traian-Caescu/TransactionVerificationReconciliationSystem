package org.example.dto;

public class TransactionDTO {

    private String transactionId;
    private double price;
    private int quantity;
    private String uid;
    private String status;

    // Constructors, Getters, and Setters

    public TransactionDTO() {}

    public TransactionDTO(String transactionId, double price, int quantity, String uid, String status) {
        this.transactionId = transactionId;
        this.price = price;
        this.quantity = quantity;
        this.uid = uid;
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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
}
