package org.example.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TransactionDTO {

    @NotBlank(message = "Transaction ID is mandatory")
    private String transactionId;

    @NotNull(message = "Price is mandatory")
    private Double price;

    @NotNull(message = "Quantity is mandatory")
    private Integer quantity;

    @NotBlank(message = "UID is mandatory")
    private String uid;

    private String status;

    // Constructors
    public TransactionDTO() {}

    public TransactionDTO(String transactionId, Double price, Integer quantity, String uid, String status) {
        this.transactionId = transactionId;
        this.price = price;
        this.quantity = quantity;
        this.uid = uid;
        this.status = status;
    }

    // Getters and Setters
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
}
